package ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.COMMENTS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.NOTIFICATIONS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val blockChainRepository: BlockChainRepository
) :
    CommentRepository {

    override fun getCommentsListener(
        policyId: String,
        language: AppLanguage,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration {
        val targetLang = language.toTargetLang()

        return firestore.collection(POLICIES_REF)
            .document(policyId)
            .collection(COMMENTS_REF)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Optionally handle error here if needed
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val originalComments = snapshot?.toObjects(Comment::class.java) ?: emptyList()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val translatedComments = originalComments.map { comment ->
                            translateCommentToLanguage(comment, targetLang)
                        }
                        withContext(Dispatchers.Main) {
                            onUpdate(translatedComments)
                        }
                    } catch (e: Exception) {
                        // Fallback to original comments if translation fails
                        withContext(Dispatchers.Main) {
                            onUpdate(originalComments)
                        }
                    }
                }
            }
    }


    override suspend fun addComment(policyId: String, comment: Comment) {
        firestore.collection(POLICIES_REF)
            .document(policyId)
            .collection(COMMENTS_REF)
            .add(comment)
            .addOnSuccessListener { blockChainRepository.createBlockchainTransaction(
                TransactionTypes.COMMENT_ON_POLICY) }
    }

    private suspend fun translateCommentToLanguage(
        comment: Comment,
        targetLang: String
    ): Comment {
        return comment.copy(
            comment = translateTextWithMLKit(comment.comment, targetLang)
        )
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.COMMENTS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    CommentRepository {

    override fun getCommentsListener(
        policyId: String,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration {
        return firestore.collection(POLICIES_REF)
            .document(policyId)
            .collection(COMMENTS_REF)
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val comments = snapshot?.toObjects(Comment::class.java) ?: emptyList()
                onUpdate(comments)
            }
    }
    override suspend fun addComment(policyId: String, comment: Comment) {
        firestore.collection(POLICIES_REF)
            .document(policyId)
            .collection(COMMENTS_REF)
            .add(comment)
    }
}
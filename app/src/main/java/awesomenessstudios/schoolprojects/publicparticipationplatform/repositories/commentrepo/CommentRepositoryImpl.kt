package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.commentrepo

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Comment
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.COMMENTS_REF
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.POLICIES_REF
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
}
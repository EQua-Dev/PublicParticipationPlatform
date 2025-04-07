package ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import com.google.firebase.firestore.ListenerRegistration

interface CommentRepository {
    fun getCommentsListener(
        policyId: String,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration
}
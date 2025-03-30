package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.commentrepo

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Comment
import com.google.firebase.firestore.ListenerRegistration

interface CommentRepository {
    fun getCommentsListener(
        policyId: String,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration
}
package ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import com.google.firebase.firestore.ListenerRegistration
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

interface CommentRepository {
    fun getCommentsListener(
        policyId: String,
        language: AppLanguage,
        onUpdate: (List<Comment>) -> Unit
    ): ListenerRegistration
    suspend fun addComment(policyId: String, comment: Comment)

}
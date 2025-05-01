package ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo

import com.google.firebase.firestore.ListenerRegistration
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll

interface NotificationRepository {
    fun getUserNotificationsRealtime(
        userId: String,
        onResult: (List<AppNotification>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration

    suspend fun sendPetitionSignNotifications(
        petition: Petition,
        newSignerId: String
    )

    suspend fun sendPollVoteNotifications(
        poll: Poll,
        newVoterId: String,
    )

    suspend fun sendBudgetVoteNotifications(
        budget: Budget,
        newVoterId: String,
    )

    suspend fun sendPolicyCommentNotifications(policyId: String, newCommenterId: String)
}

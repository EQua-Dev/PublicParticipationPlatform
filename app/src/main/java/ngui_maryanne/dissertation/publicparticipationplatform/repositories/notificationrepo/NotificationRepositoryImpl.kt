package ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.COMMENTS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.NOTIFICATIONS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF

class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

//    private var listenerRegistration: ListenerRegistration? = null

    override fun getUserNotificationsRealtime(
        userId: String,
        onResult: (List<AppNotification>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return firestore.collection(NOTIFICATIONS_REF)
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val notifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(AppNotification::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onResult(notifications)
                } else {
                    onResult(emptyList())
                }
            }
    }

    override suspend fun sendPetitionSignNotifications(
        petition: Petition,
        newSignerId: String
    ) {
        val notificationsRef = firestore.collection(NOTIFICATIONS_REF)
        val now = System.currentTimeMillis().toString()

        val batch = firestore.batch()

        // 1. Notify the petition creator (if not the one signing it)
        if (petition.createdBy != newSignerId) {
            val ownerNotification = AppNotification(
                receiverId = petition.createdBy,
                type = NotificationTypes.PETITION,
                typeId = petition.id,
                dateCreated = now,
                message = "Someone just signed your petition: \"${petition.title}\""
            )
            val docRef = notificationsRef.document()
            batch.set(docRef, ownerNotification)
        }

        // 2. Notify previous signers (excluding the creator and the new signer)
        val notifiedUserIds = mutableSetOf<String>() // avoid duplicates

        petition.signatures.forEach { signature ->
            val userId = signature.userId
            if (userId != newSignerId && userId != petition.createdBy && notifiedUserIds.add(userId)) {
                val signerNotification = AppNotification(
                    receiverId = userId,
                    type = NotificationTypes.PETITION,
                    typeId = petition.id,
                    dateCreated = now,
                    message = "Someone else just signed the petition you supported: \"${petition.title}\""
                )
                val docRef = notificationsRef.document()
                batch.set(docRef, signerNotification)
            }
        }

        // Commit the batch write
        batch.commit().await()
    }

    override suspend fun sendPollVoteNotifications(
        poll: Poll,
        newVoterId: String,
    ) {
        val notificationsRef = firestore.collection(NOTIFICATIONS_REF)
        val now = System.currentTimeMillis().toString()

        val batch = firestore.batch()
        val notifiedUserIds = mutableSetOf<String>()

        poll.responses.forEach { response ->
            val userId = response.userId
            if (userId != newVoterId && notifiedUserIds.add(userId)) {
                val notification = AppNotification(
                    receiverId = userId,
                    type = NotificationTypes.POLL,
                    typeId = poll.id,
                    dateCreated = now,
                    message = "Someone else just voted on the poll you participated in: \"${poll.pollQuestion}\""
                )
                val docRef = notificationsRef.document()
                batch.set(docRef, notification)
            }
        }

        batch.commit().await()
    }

    override suspend fun sendBudgetVoteNotifications(
        budget: Budget,
        newVoterId: String,
    ) {
        val notificationsRef = firestore.collection(NOTIFICATIONS_REF)
        val now = System.currentTimeMillis().toString()

        val batch = firestore.batch()
        val notifiedUserIds = mutableSetOf<String>()

        budget.responses.forEach { response ->
            val userId = response.userId
            if (userId != newVoterId && notifiedUserIds.add(userId)) {
                val notification = AppNotification(
                    receiverId = userId,
                    type = NotificationTypes.BUDGET,
                    typeId = budget.id,
                    dateCreated = now,
                    message = "Someone else just voted on the budget you participated in: \"Budget #${budget.budgetNo}\""
                )
                val docRef = notificationsRef.document()
                batch.set(docRef, notification)
            }
        }

        batch.commit().await()
    }

    override suspend fun sendPolicyCommentNotifications(policyId: String, newCommenterId: String) {
        val notificationsRef = firestore.collection(NOTIFICATIONS_REF)
        val now = System.currentTimeMillis().toString()

        val commentsSnapshot = firestore.collection(POLICIES_REF)
            .document(policyId)
            .collection(COMMENTS_REF)
            .get()
            .await()

        val batch = firestore.batch()
        val notifiedUserIds = mutableSetOf<String>()

        for (doc in commentsSnapshot.documents) {
            val userId = doc.getString("userId") ?: continue

            if (userId != newCommenterId && notifiedUserIds.add(userId)) {
                val notification = AppNotification(
                    receiverId = userId,
                    type = NotificationTypes.POLICY,
                    typeId = policyId,
                    dateCreated = now,
                    message = "Someone else also commented on the policy you're participating in."
                )
                val docRef = notificationsRef.document()
                batch.set(docRef, notification)
            }
        }

        batch.commit().await()
    }


    /*  fun removeListener() {
          listenerRegistration?.remove()
          listenerRegistration = null
      }*/
}

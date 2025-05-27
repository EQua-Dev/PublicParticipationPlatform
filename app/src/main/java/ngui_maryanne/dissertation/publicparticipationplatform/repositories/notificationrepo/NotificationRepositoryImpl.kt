package ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.mlkit.nl.translate.TranslateLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.COMMENTS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.NOTIFICATIONS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.OFFICIALS_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants.POLICIES_REF
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import kotlin.coroutines.resumeWithException

class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val translatorProvider: TranslatorProvider
) : NotificationRepository {

//    private var listenerRegistration: ListenerRegistration? = null

    override fun getUserNotificationsRealtime(
        userId: String,
        language: AppLanguage,
        onResult: (List<AppNotification>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }


        return firestore.collection(NOTIFICATIONS_REF)
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val originalNotifications = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(AppNotification::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    // Translate notifications in background thread
                    CoroutineScope(Dispatchers.IO).launch {
                        val translatedNotifications = originalNotifications.map { notification ->
                            translateNotificationToLanguage(notification, targetLang)
                        }
                        // Return result on main thread
                        withContext(Dispatchers.Main) {
                            onResult(translatedNotifications)
                        }
                    }
                } else {
                    onResult(emptyList())
                }
            }
    }

    override suspend fun sendCitizenRegistrationNotifications(
        citizen: Citizen,
    ) {
        val notificationsRef = firestore.collection(NOTIFICATIONS_REF)
        val now = System.currentTimeMillis().toString()
        val batch = firestore.batch()

        // Step 1: Get all officials
        val officialsSnapshot = firestore.collection(OFFICIALS_REF).get().await()

        // Step 2: Filter those with 'approve_citizens' permission
        val approvingOfficials = officialsSnapshot.documents
            .mapNotNull { it.toObject(Official::class.java) }
            .filter { "approve_citizens" in it.permissions }

        // Step 3: Create notifications
        approvingOfficials.forEach { official ->
            val notification = AppNotification(
                receiverId = official.id,
                type = NotificationTypes.CITIZEN_REGISTRATION,
                typeId = citizen.id,
                dateCreated = now,
                message = "A new citizen just registered. Go to Citizen screen to verify"
            )
            val docRef = notificationsRef.document()
            batch.set(docRef, notification)
        }

        // Step 4: Commit batch
        batch.commit().await()
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

    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val translator = translatorProvider.getTranslator(sourceLang, targetLang)
        return suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }


    private suspend fun translateNotificationToLanguage(
        notification: AppNotification,
        targetLang: String
    ): AppNotification {

        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }
        return notification.copy(
            message = translateText(notification.message, sourceLang, targetLang)
        )
    }
}

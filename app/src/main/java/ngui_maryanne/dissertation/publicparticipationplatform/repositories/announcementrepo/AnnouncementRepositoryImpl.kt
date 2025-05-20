package ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo

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
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import kotlin.coroutines.resumeWithException

class AnnouncementRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val translatorProvider: TranslatorProvider
) : AnnouncementRepository {

    private val announcementsRef = firestore.collection(Constants.ANNOUNCEMENTS_REF)

    override suspend fun addAnnouncement(
        announcement: Announcement,
        notificationType: NotificationTypes,
    ) {

        val newAnnouncement = announcement.copy(
            type = notificationType,
            createdAt = System.currentTimeMillis().toString() // Use current timestamp for createdAt
        )
        // Add to Firestore
        announcementsRef.document().set(newAnnouncement)
            .await()


    }


    override fun getAllAnnouncementsRealtime(
        language: AppLanguage,
        onResult: (List<Announcement>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val targetLang = when (language) {
            AppLanguage.SWAHILI -> TranslateLanguage.SWAHILI
            AppLanguage.ENGLISH -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        return announcementsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val originalAnnouncements = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Announcement::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }

                // Translate in background thread
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val translatedAnnouncements = originalAnnouncements.map { announcement ->
                            translateAnnouncementToLanguage(announcement, targetLang)
                        }
                        // Return to main thread
                        withContext(Dispatchers.Main) {
                            onResult(translatedAnnouncements)
                        }
                    } catch (e: Exception) {
                        // Fallback to original if translation fails
                        withContext(Dispatchers.Main) {
                            onResult(originalAnnouncements)
                        }
                    }
                }
            } else {
                onResult(emptyList())
            }
        }
    }


    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val translator = translatorProvider.getTranslator(sourceLang, targetLang)
        return suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }


    private suspend fun translateAnnouncementToLanguage(
        announcement: Announcement,
        targetLang: String
    ): Announcement {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }
        return announcement.copy(
            title = translateText(announcement.title, sourceLang, targetLang),
            description = translateText(announcement.description, sourceLang, targetLang)
            // Other fields (IDs, dates, etc.) remain unchanged
        )
    }
}

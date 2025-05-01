package ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Constants

class AnnouncementRepositoryImpl(
    private val firestore: FirebaseFirestore
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
        onResult: (List<Announcement>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Real-time listener for announcements
        announcementsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val announcements = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Announcement::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                onResult(announcements)
            } else {
                onResult(emptyList())
            }
        }
    }
}

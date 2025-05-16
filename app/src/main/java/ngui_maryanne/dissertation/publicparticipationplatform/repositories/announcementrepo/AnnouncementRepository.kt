package ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo

import com.google.firebase.firestore.ListenerRegistration
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

interface AnnouncementRepository {
    suspend fun addAnnouncement(
        announcement: Announcement,
        notificationType: NotificationTypes,
    )

    fun getAllAnnouncementsRealtime(
        language: AppLanguage,
        onResult: (List<Announcement>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration
}

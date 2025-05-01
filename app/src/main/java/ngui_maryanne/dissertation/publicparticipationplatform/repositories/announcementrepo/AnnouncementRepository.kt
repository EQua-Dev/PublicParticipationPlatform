package ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement

interface AnnouncementRepository {
    suspend fun addAnnouncement(
        announcement: Announcement,
        notificationType: NotificationTypes,
    )

    fun getAllAnnouncementsRealtime(
        onResult: (List<Announcement>) -> Unit,
        onError: (Exception) -> Unit
    )
}

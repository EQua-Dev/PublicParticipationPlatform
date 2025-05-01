package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes

data class Announcement(
    val id: String = "",
    val createdBy: String = "",
    val hash: String = "",
    val createdAt: String = "",
    val type: NotificationTypes = NotificationTypes.UNKNOWN,
    val typeId: String = "",
    val title: String = "",
    val description: String = ""
)

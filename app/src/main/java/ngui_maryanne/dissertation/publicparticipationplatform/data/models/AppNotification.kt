package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes

data class AppNotification(
    val id: String = "",
    val receiverId: String = "",
    val type: NotificationTypes = NotificationTypes.UNKNOWN,
    val typeId: String = "",
    val message: String = "",
    val dateCreated: String = ""

)
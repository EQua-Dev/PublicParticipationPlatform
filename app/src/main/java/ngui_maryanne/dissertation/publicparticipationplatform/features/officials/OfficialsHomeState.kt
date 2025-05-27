package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

data class OfficialsHomeState(
    val official: Official? = null,
    val notifications: MutableList<AppNotification> = mutableListOf(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val logout: Boolean = false
)

package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

data class OfficialsHomeState(
    val citizen: Citizen? = null,
    val isApproved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val logout: Boolean = false
)

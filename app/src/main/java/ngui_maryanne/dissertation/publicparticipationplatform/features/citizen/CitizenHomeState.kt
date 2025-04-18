package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

data class CitizenHomeState(
    val citizen: Citizen? = null,
    val isApproved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val logout: Boolean = false
)

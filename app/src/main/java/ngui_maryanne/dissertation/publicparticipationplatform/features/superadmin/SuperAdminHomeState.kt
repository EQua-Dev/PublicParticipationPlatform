package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

data class SuperAdminHomeState(
    val citizen: Citizen? = null,
    val isApproved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val logout: Boolean = false
)

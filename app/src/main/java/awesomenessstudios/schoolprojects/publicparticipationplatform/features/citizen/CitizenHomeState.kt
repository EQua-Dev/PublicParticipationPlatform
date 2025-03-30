package awesomenessstudios.schoolprojects.publicparticipationplatform.features.citizen

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen

data class CitizenHomeState(
    val citizen: Citizen? = null,
    val isApproved: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

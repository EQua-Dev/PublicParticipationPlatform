package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.citizens

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.NationalCitizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official

data class CitizenState(
    val citizens: List<Citizen> = emptyList(),
    val official: Official? = null,
    val selectedCitizen: Citizen? = null,
    val nationalCitizen: NationalCitizen? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showBottomSheet: Boolean = false
)
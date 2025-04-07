package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.NationalCitizen
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

data class CitizenState(
    val citizens: List<Citizen> = emptyList(),
    val official: Official? = null,
    val selectedCitizen: Citizen? = null,
    val nationalCitizen: NationalCitizen? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showBottomSheet: Boolean = false
)
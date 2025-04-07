package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

sealed class CitizenEvent {
    object LoadData : CitizenEvent()
    data class SelectCitizen(val citizen: Citizen) : CitizenEvent()
    object ApproveCitizen : CitizenEvent()
    object RejectCitizen : CitizenEvent()
    object DismissBottomSheet : CitizenEvent()
}

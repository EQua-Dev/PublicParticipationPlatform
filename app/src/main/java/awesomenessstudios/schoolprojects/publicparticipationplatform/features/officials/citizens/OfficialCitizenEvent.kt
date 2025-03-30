package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.citizens

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen

sealed class CitizenEvent {
    object LoadData : CitizenEvent()
    data class SelectCitizen(val citizen: Citizen) : CitizenEvent()
    object ApproveCitizen : CitizenEvent()
    object RejectCitizen : CitizenEvent()
    object DismissBottomSheet : CitizenEvent()
}

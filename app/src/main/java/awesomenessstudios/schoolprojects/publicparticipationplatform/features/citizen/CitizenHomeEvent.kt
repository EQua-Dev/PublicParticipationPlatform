package awesomenessstudios.schoolprojects.publicparticipationplatform.features.citizen

sealed class CitizenHomeEvent {
    object LoadCitizenData : CitizenHomeEvent()
    object NavigateToProfile : CitizenHomeEvent()
    object Logout : CitizenHomeEvent()
}
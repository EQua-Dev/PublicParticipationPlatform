package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

sealed class CitizenHomeEvent {
    object LoadCitizenData : CitizenHomeEvent()
    object NavigateToProfile : CitizenHomeEvent()
    object Logout : CitizenHomeEvent()
}
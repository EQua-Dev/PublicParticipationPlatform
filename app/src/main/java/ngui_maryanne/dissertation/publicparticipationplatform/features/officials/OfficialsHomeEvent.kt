package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

sealed class OfficialsHomeEvent {
    object LoadCitizenData : OfficialsHomeEvent()
    object NavigateToProfile : OfficialsHomeEvent()
    object Logout : OfficialsHomeEvent()
}
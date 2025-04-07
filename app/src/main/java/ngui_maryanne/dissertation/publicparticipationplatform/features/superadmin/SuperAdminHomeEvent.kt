package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin

sealed class SuperAdminHomeEvent {
    object LoadCitizenData : SuperAdminHomeEvent()
    object NavigateToProfile : SuperAdminHomeEvent()
    object Logout : SuperAdminHomeEvent()
}
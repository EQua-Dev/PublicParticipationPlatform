package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.presentation

// UI Event sealed class
sealed class SuperAdminUiEvent {
    data class SelectTab(val index: Int) : SuperAdminUiEvent()
    data class ToggleFabMenu(val isExpanded: Boolean) : SuperAdminUiEvent()
}
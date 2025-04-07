package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies

sealed class PolicyEvent {
    object LoadPolicies : PolicyEvent()
    object NavigateToCreatePolicy : PolicyEvent()
}
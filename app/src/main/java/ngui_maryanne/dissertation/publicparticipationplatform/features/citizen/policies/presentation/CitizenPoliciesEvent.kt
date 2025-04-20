package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

sealed class CitizenPoliciesAction {
    data class OnSearchQueryChanged(val query: String) : CitizenPoliciesAction()
    object OnBackClicked : CitizenPoliciesAction()
    data class OnPolicyClicked(val policyId: String) : CitizenPoliciesAction()
}

sealed class CitizenPoliciesEvent {
    object NavigateBack : CitizenPoliciesEvent()
    data class NavigateToPolicyDetails(val policyId: String) : CitizenPoliciesEvent()
}
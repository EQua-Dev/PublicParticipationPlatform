package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

sealed class CitizenPoliciesAction {
    data class OnSearchQueryChanged(val query: String) : CitizenPoliciesAction()
    object OnBackClicked : CitizenPoliciesAction()
    data class OnPolicyClicked(val policyId: String) : CitizenPoliciesAction()
    data class OnStatusFilterChanged(val status: PolicyStatus?) : CitizenPoliciesAction()
    object LoadPolicies : CitizenPoliciesAction()  // Explicit load action

}

sealed class CitizenPoliciesEvent {
    object NavigateBack : CitizenPoliciesEvent()
    data class NavigateToPolicyDetails(val policyId: String) : CitizenPoliciesEvent()

    data class ShowError(val message: String) : CitizenPoliciesEvent()  // For snackbar errors
    object ClearError : CitizenPoliciesEvent()  // To clear error state
}
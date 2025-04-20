package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy

data class CitizenPoliciesUiState(
    val policies: List<Policy> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
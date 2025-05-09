package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy

data class CitizenPoliciesUiState(
    val policies: List<Policy> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: PolicyStatus? = null,  // Add status filter
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmptyState: Boolean = false  // Explicit empty state flag
)
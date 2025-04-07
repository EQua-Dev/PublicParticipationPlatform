package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy

data class PolicyState(
    val policies: List<Policy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canCreatePolicy: Boolean = false
)
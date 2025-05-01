package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll

data class CitizenPollsUiState(
    val polls: List<PollWithPolicyName> = emptyList(),
    val allPolls: List<PollWithPolicyName> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentUserRole: String = "citizen",
)

data class PollWithPolicyName(
    val poll: Poll,
    val policyName: String
)
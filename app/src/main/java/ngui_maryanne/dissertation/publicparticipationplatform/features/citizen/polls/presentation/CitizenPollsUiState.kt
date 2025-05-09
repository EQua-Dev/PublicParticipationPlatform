package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import java.time.LocalDateTime

// UI State
data class CitizenPollsUiState(
    val polls: List<PollWithPolicyName> = emptyList(),
    val allPolls: List<PollWithPolicyName> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: PollStatus? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentUserRole: UserRole = UserRole.CITIZEN,
    val lastUpdated: LocalDateTime? = null
)

data class PollWithPolicyName(
    val poll: Poll,
    val policyName: String,
    val policyStatus: PolicyStatus? = null
) {
    val isActive: Boolean get() = poll.pollExpiry.toLong() > System.currentTimeMillis()
    val pollStatus: PollStatus get() = if (isActive) PollStatus.ACTIVE else PollStatus.CLOSED
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses

data class PollDetailsUiState(
    val poll: Poll? = null,
    val policy: Policy? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val currentUserRole: String = "citizen",

    val hasVoted: Boolean = false,
    val userVoteOptionId: String? = null,
    val budgetOptions: List<PollResponses> = emptyList(),
    val votedOptionId: String? = null, // null means not yet voted
)

package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption

data class BudgetDetailsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val editSuccess: Boolean = false,
    val message: String? = null,
    val budget: Budget? = null,
    val hasVoted: Boolean = false,
    val userVoteOptionId: String? = null,
    val budgetOptions: List<BudgetOption> = emptyList(),
    val votedOptionId: String? = null, // null means not yet voted
)

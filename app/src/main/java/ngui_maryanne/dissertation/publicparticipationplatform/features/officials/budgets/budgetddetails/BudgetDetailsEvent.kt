package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

sealed class BudgetDetailsEvent {
    data class LoadBudget(val budgetId: String) : BudgetDetailsEvent()
    data class VoteOption(val optionId: String) : BudgetDetailsEvent()
    object ToggleActivation : BudgetDetailsEvent()
    object OnErrorShown : BudgetDetailsEvent()

    // UI State handling
//    data class SetError(val message: String) : BudgetDetailsEvent()
//    object ClearError : BudgetDetailsEvent()
}

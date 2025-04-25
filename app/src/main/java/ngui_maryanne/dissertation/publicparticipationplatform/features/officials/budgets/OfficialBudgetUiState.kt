package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget

data class OfficialBudgetUiState(
    val isLoading: Boolean = false,
    val creationSuccess: Boolean = false,
    val error: String? = null,
    val budgets: List<Budget> = emptyList(),
    val amount: String = "",
    val budgetNote: String = "",
    val impact: String = "",
    val budgetOptions: List<BudgetOptionInput> = listOf(),
    val currentUserRole: String = "official",
    val navigateToCreateBudget: Boolean = false // for FAB navigation

)

data class BudgetOptionInput(
    val projectName: String = "",
    val description: String = "",
    val associatedPolicy: String = "",
    val amount: String = "",
    val imageUri: Uri? = null // Needs to be added by the UI
)

package ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo

import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature

interface BudgetRepository {
    suspend fun createBudget(budget: Budget)
    fun getAllBudgets(): Flow<List<Budget>>
    fun getBudgetById(id: String): Flow<Budget?>
    suspend fun submitBudgetResponse(response: BudgetResponse)
    suspend fun voteForBudgetOption(budgetId: String, updatedResponses: MutableList<BudgetResponse>)
    suspend fun toggleBudgetActivation(budgetId: String, isActive: Boolean)
    suspend fun updateBudgetDetails(budgetId: String, updatedFields: Map<String, Any>)

}

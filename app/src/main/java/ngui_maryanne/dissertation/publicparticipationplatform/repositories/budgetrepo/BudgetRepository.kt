package ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo

import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

interface BudgetRepository {
    suspend fun createBudget(budget: Budget)
    fun getAllBudgets( language: AppLanguage): Flow<List<Budget>>
    fun getBudgetById(id: String,  language: AppLanguage): Flow<Budget?>
    suspend fun submitBudgetResponse(response: BudgetResponse)
    suspend fun voteForBudgetOption(budgetId: String, updatedResponses: MutableList<BudgetResponse>)
    suspend fun toggleBudgetActivation(budgetId: String, isActive: Boolean)
    suspend fun updateBudgetDetails(budgetId: String, updatedFields: Map<String, Any>)

}

package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OfficialBudgetDetailsViewModel @Inject constructor(
    private val repository: BudgetRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _uiState = mutableStateOf(BudgetDetailsState())
    val uiState: State<BudgetDetailsState> = _uiState

    fun onEvent(event: BudgetDetailsEvent) {
        when (event) {
            is BudgetDetailsEvent.LoadBudget -> {
                loadBudget(event.budgetId)
            }

            is BudgetDetailsEvent.VoteOption -> {
                voteForOption(event.optionId)
            }

            is BudgetDetailsEvent.ToggleActivation -> {
                toggleActivation()
            }

            is BudgetDetailsEvent.OnErrorShown -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
        }
    }

    private fun loadBudget(budgetId: String) {
        viewModelScope.launch {
            repository.getBudgetById(budgetId).collect { budget ->
                if (budget != null) {
                    val votedOption = budget.responses.find { it.userId == auth.currentUser!!.uid }
                    val votedOptionId = votedOption?.optionId

                    _uiState.value = _uiState.value.copy(
                        budget = budget,
                        budgetOptions = budget.budgetOptions,
                        votedOptionId = votedOptionId,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Budget not found",
                        isLoading = false
                    )
                }
            }
        }
    }


    private fun voteForOption(optionId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            try {

                // Call the repository function to vote for a budget option
                val voteOption = BudgetResponse(
                    answerId = UUID.randomUUID().toString(),
                            answerHash = "",
                            userId = auth.currentUser!!.uid,
                            optionId = optionId,
//                            isAnonymous =
                            dateCreated = System.currentTimeMillis().toString()
                )
                repository.voteForBudgetOption(currentState.budget!!.id, voteOption)

                // Update the UI state to reflect the voted option
                _uiState.value = currentState.copy(votedOptionId = optionId)
            } catch (e: Exception) {
                // If an error occurs, update the UI state with the error message
                _uiState.value = currentState.copy(error = e.message ?: "Vote failed")
            }
        }
    }


    private fun toggleActivation() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                // Toggle the activation status of the budget
                repository.toggleBudgetActivation(currentState.budget!!.id, !currentState.budget!!.isActive)

                // Update the UI state to reflect the new activation status
//                _uiState.value = currentState.copy(isActive = !currentState.isActive)
            } catch (e: Exception) {
                // If there's an error, update the UI state with the error message
                _uiState.value = _uiState.value.copy(error = e.message ?: "Could not update budget status")
            }
        }
    }

}

package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import javax.inject.Inject

@HiltViewModel
class CitizenPoliciesViewModel @Inject constructor(
    private val repository: PolicyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitizenPoliciesUiState())
    val uiState: StateFlow<CitizenPoliciesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CitizenPoliciesEvent>()
    val events: SharedFlow<CitizenPoliciesEvent> = _events

    private var searchJob: Job? = null

    init {
        loadPolicies()
    }

    fun handleAction(action: CitizenPoliciesAction) {
        when (action) {
            is CitizenPoliciesAction.OnSearchQueryChanged -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    _uiState.update { it.copy(searchQuery = action.query) }
                    if (action.query.isBlank()) {
                        loadPolicies()
                    } else {
                        searchPolicies(action.query)
                    }
                }
            }
            CitizenPoliciesAction.OnBackClicked -> {
                viewModelScope.launch {
                    _events.emit(CitizenPoliciesEvent.NavigateBack)
                }
            }
            is CitizenPoliciesAction.OnPolicyClicked -> {
                viewModelScope.launch {
                    _events.emit(CitizenPoliciesEvent.NavigateToPolicyDetails(action.policyId))
                }
            }

            CitizenPoliciesAction.LoadPolicies ->  {
                loadPolicies()
            }
            is CitizenPoliciesAction.OnStatusFilterChanged -> {
                _uiState.value = _uiState.value.copy(selectedStatus = action.status)
                loadPolicies()
            }
        }
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getPublicPolicies().collect { policies ->
                    _uiState.update {
                        it.copy(
                            policies = policies,
                            isLoading = false,
                            error = null,
                            isEmptyState = policies.isEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading policies"
                    )
                }
                _events.emit(CitizenPoliciesEvent.ShowError(
                    _uiState.value.error ?: "An error occurred"
                ))
            }
        }
    }

    private fun searchPolicies(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.searchPolicies(query).collect { policies ->
                    _uiState.update {
                        it.copy(
                            policies = policies,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error searching policies"
                    )
                }
            }
        }
    }
}
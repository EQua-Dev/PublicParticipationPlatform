package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class CitizenPoliciesViewModel @Inject constructor(
    private val repository: PolicyRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(CitizenPoliciesUiState())
    val uiState: StateFlow<CitizenPoliciesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CitizenPoliciesEvent>()
    val events: SharedFlow<CitizenPoliciesEvent> = _events

    private var searchJob: Job? = null


    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    Log.d("TAG", "selected language: ${_selectedLanguage.value}")
//                    observePolls(lang)
                    loadPolicies(lang)
                }

//            observePolls()
        }
    }


  /*  init {
        loadPolicies(lang)
    }*/

    fun handleAction(action: CitizenPoliciesAction) {
        when (action) {
            is CitizenPoliciesAction.OnSearchQueryChanged -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    _uiState.update { it.copy(searchQuery = action.query) }
                    if (action.query.isBlank()) {
                        loadPolicies(_selectedLanguage.value)
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
//                loadPolicies(lang)
            }
            is CitizenPoliciesAction.OnStatusFilterChanged -> {
                _uiState.value = _uiState.value.copy(selectedStatus = action.status)
                loadPolicies(_selectedLanguage.value)
            }
        }
    }

    private fun loadPolicies(lang: AppLanguage) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getPublicPolicies(_selectedLanguage.value).collect { policies ->
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
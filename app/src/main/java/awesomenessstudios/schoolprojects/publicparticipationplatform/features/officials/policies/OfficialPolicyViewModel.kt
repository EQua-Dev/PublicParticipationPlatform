package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.OfficialsRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.PolicyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PolicyViewModel @Inject constructor(
    private val policyRepository: PolicyRepository,
    private val officialRepository: OfficialsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PolicyState())
    val state: StateFlow<PolicyState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: PolicyEvent) {
        when (event) {
            PolicyEvent.LoadPolicies -> loadData()
            PolicyEvent.NavigateToCreatePolicy -> { /* Handled in UI */ }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val official = officialRepository.getCurrentOfficial()
                val policies = policyRepository.getAllPolicies()
                _state.value = _state.value.copy(
                    policies = policies,
                    canCreatePolicy = official.permissions.contains("create_policy"),
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
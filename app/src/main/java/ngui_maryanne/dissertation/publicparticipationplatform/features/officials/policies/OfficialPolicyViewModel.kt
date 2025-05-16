package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class PolicyViewModel @Inject constructor(
    userPreferences: UserPreferences,
    private val policyRepository: PolicyRepository,
    private val officialRepository: OfficialsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PolicyState())
    val state: StateFlow<PolicyState> = _state.asStateFlow()

    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                }
        }
    }

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
                policyRepository.getAllPolicies(_selectedLanguage.value).collect{ policies ->
                    _state.value = _state.value.copy(
                        policies = policies,
                        canCreatePolicy = official.permissions.contains("create_policy"),
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
}
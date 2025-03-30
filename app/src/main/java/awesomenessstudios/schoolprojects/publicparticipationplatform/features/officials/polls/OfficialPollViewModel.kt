package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.polls

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val pollRepository: PollsRepository,
    private val policyRepository: PolicyRepository,
    private val officialRepository: OfficialsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PollState())
    val state: StateFlow<PollState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: PollEvent) {
        when (event) {
            PollEvent.LoadData -> loadData()
            PollEvent.NavigateToCreatePoll -> { /* Handled in UI */ }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val official = officialRepository.getCurrentOfficial()
                val policies = policyRepository.getPoliciesBeforePublicOpinion()
                val polls = pollRepository.getAllPolls()

                _state.value = _state.value.copy(
                    polls = polls,
                    policies = policies,
                    canCreatePoll = official.permissions.contains("create_poll"),
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
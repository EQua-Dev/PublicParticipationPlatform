package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.PollWithPolicyName
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val pollRepository: PollsRepository,
    private val policyRepository: PolicyRepository,
    private val officialRepository: OfficialsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PollState())
    val state: StateFlow<PollState> = _state.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

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


  /*  private fun observePolls() {
        listenerRegistration = pollRepository.getAllPollsListener { polls ->
            viewModelScope.launch {
                val pollsWithPolicyName = polls.map { poll ->
                    val policy = pollRepository.getPolicySnapshot(poll.policyId)
                    Log.d("Get Poll Policy", "observePolls: $policy")
                    PollWithPolicyName(
                        poll = poll,
                        policyName = policy?.policyTitle ?: "Unknown Policy"
                    )
                }

                val filtered = applySearchFilter(pollsWithPolicyName, _uiState.value.searchQuery)

                _uiState.update {
                    it.copy(polls = filtered, allPolls = pollsWithPolicyName)
                }
            }
        }
    }*/

}
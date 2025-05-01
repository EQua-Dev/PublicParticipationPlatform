package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class CitizenPollsViewModel @Inject constructor(
    private val repository: PollsRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenPollsUiState())
    val uiState: StateFlow<CitizenPollsUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null


    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                if (role != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUserRole = role.name
                    )
                }
            }
        }
    }

    init {
        observePolls()
    }

    private fun observePolls() {
        listenerRegistration = repository.getAllPollsListener { polls ->
            viewModelScope.launch {
                val pollsWithPolicyName = polls.map { poll ->
                    val policy = repository.getPolicySnapshot(poll.policyId)
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
    }

    /* private fun loadPolls() {
         listeners += repository.getAllPollsListener { polls ->
             viewModelScope.launch {
                 val pollsWithPolicyName = polls.map { poll ->
                     val policy = repository.getPolicySnapshot(poll.policyId)
                     PollWithPolicyName(
                         poll = poll,
                         policyName = policy?.policyTitle ?: "Unknown Policy"
                     )
                 }

                 _uiState.value = _uiState.value.copy(
                     polls = pollsWithPolicyName,
                     isLoading = false,
                     error = null
                 )
             }
         }
     }
 */


    fun onEvent(event: CitizenPollsEvent) {
        when (event) {
            is CitizenPollsEvent.OnPollClicked -> {
                // Handle navigation or other actions in the screen via a callback
            }
            is CitizenPollsEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                val filtered = applySearchFilter(_uiState.value.allPolls, event.query)
                _uiState.update { it.copy(polls = filtered) }
            }
        }
    }

    private fun applySearchFilter(
        polls: List<PollWithPolicyName>,
        query: String
    ): List<PollWithPolicyName> {
        return if (query.isBlank()) polls
        else polls.filter {
            it.poll.pollQuestion.contains(query, ignoreCase = true) ||
                    it.policyName.contains(query, ignoreCase = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.time.LocalDateTime
import javax.inject.Inject
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CitizenPollsViewModel @Inject constructor(
    private val repository: PollsRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenPollsUiState())
    val uiState: StateFlow<CitizenPollsUiState> = _uiState.asStateFlow()

    private val _events = Channel<CitizenPollsEvent>()
    val events = _events.receiveAsFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                _uiState.update { it.copy(currentUserRole = role ?: UserRole.CITIZEN) }
            }
        }
        observePolls()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observePolls() {
        listenerRegistration?.remove()

        listenerRegistration = repository.getAllPollsListener { polls ->
            viewModelScope.launch {
                try {
                    val pollsWithPolicyName = polls.mapNotNull { poll ->
                        try {
                            val policy = repository.getPolicySnapshot(poll.policyId)
                            PollWithPolicyName(
                                poll = poll,
                                policyName = policy?.policyTitle ?: "Unknown Policy",
                                policyStatus = policy?.policyStatus
                            )
                        } catch (e: Exception) {
                            null // Skip corrupted polls
                        }
                    }

                    val filtered = applyFilters(
                        pollsWithPolicyName,
                        _uiState.value.searchQuery,
                        _uiState.value.selectedStatus
                    )

                    _uiState.update {
                        it.copy(
                            polls = filtered,
                            allPolls = pollsWithPolicyName,
                            isLoading = false,
                            error = null,
                            lastUpdated = LocalDateTime.now()
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load polls"
                        )
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: CitizenPollsEvent) {
        when (event) {
            is CitizenPollsEvent.OnPollClicked -> {
                viewModelScope.launch {
                    _events.send(event)
                }
            }

            is CitizenPollsEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }

            is CitizenPollsEvent.OnStatusFilterChanged -> {
                _uiState.update { it.copy(selectedStatus = event.status) }
                applyFilters()
            }

            CitizenPollsEvent.RefreshPolls -> {
                _uiState.update { it.copy(isLoading = true) }
                observePolls()
            }

            CitizenPollsEvent.OnErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    private fun applyFilters() {
        _uiState.update { currentState ->
            currentState.copy(
                polls = applyFilters(
                    currentState.allPolls,
                    currentState.searchQuery,
                    currentState.selectedStatus
                )
            )
        }
    }

    private fun applyFilters(
        polls: List<PollWithPolicyName>,
        query: String,
        status: PollStatus?
    ): List<PollWithPolicyName> {
        return polls.filter { poll ->
            val matchesSearch = query.isBlank() ||
                    poll.poll.pollQuestion.contains(query, ignoreCase = true) ||
                    poll.policyName.contains(query, ignoreCase = true)

            val matchesStatus = status == null || poll.pollStatus == status

            matchesSearch && matchesStatus
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import javax.inject.Inject

@HiltViewModel
class PollDetailsViewModel @Inject constructor(
    private val repository: PollsRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(PollDetailsUiState())
    val uiState: State<PollDetailsUiState> = _uiState

    fun onEvent(event: PollDetailsEvent) {
        when (event) {
            is PollDetailsEvent.LoadPollDetails -> loadPollDetails(event.pollId)
            PollDetailsEvent.Retry -> {
                uiState.value.poll?.id?.let { loadPollDetails(it) }
            }
        }
    }

    private fun loadPollDetails(pollId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                Log.d("Get Poll", "loadPollDetails: $pollId")
                val pollSnapshot = repository.getPollById(pollId)
                if (pollSnapshot != null) {
                    val policy = repository.getPolicySnapshot(pollSnapshot.policyId)
                    _uiState.value = _uiState.value.copy(
                        poll = pollSnapshot,
                        policy = policy,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Poll not found",
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.localizedMessage ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }

}

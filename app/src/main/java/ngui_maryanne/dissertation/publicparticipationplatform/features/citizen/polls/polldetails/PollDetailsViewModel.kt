package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollResponses
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.Objects.hash
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PollDetailsViewModel @Inject constructor(
    private val repository: PollsRepository,
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences,
) : ViewModel()
{

    private val _uiState = MutableStateFlow(PollDetailsUiState())
    val uiState: StateFlow<PollDetailsUiState> = _uiState.asStateFlow()

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

    fun onEvent(event: PollDetailsEvent) {
        when (event) {
            is PollDetailsEvent.LoadPollDetails -> {
                viewModelScope.launch {
                    userPreferences.languageFlow
                        .distinctUntilChanged()
                        .collect { lang ->
                            Log.d("TAG", "selected language: $lang")
                            _selectedLanguage.value = lang
                            loadPollDetails(event.pollId, lang)
                        }
                }

            }
            PollDetailsEvent.Retry -> {
                viewModelScope.launch {
                    userPreferences.languageFlow
                        .distinctUntilChanged()
                        .collect { lang ->
                            Log.d("TAG", "selected language: $lang")
                            _selectedLanguage.value = lang
                            uiState.value.poll?.id?.let { loadPollDetails(it, lang) }
                        }
                }

            }
        }
    }

    private fun loadPollDetails(pollId: String, lang: AppLanguage) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                Log.d("Get Poll", "loadPollDetails: $pollId")
                val pollSnapshot = repository.getPollById(pollId, lang)
                if (pollSnapshot != null) {
                    val policy = repository.getPolicySnapshot(pollSnapshot.policyId, lang)
                    val currentUserId = auth.currentUser!!.uid // however you get current user
                    val userResponse = pollSnapshot.responses.find { it.userId == currentUserId }
                    val votedOptionId = userResponse?.optionId

                    _uiState.value = _uiState.value.copy(
                        poll = pollSnapshot,
                        policy = policy,
                        votedOptionId = votedOptionId,
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


    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyAndVoteOption(
        activity: FragmentActivity,
        optionId: String,
        hashType: String,
        optionName: String,
//        answer1: String,
//        answer2: String,
//        securityQuestion1: String,
//        securityQuestion2: String,
//        storedSecurityHash: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        /*       val hash1 = hash(securityQuestion1 + answer1, hashType)
               val hash2 = hash(securityQuestion2 + answer2, hashType)
               val combinedHash = hash(hash1 + hash2, hashType)

               if (combinedHash != storedSecurityHash) {
                   onFailure("Security answers don't match")
                   return
               }
       */
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Voting Option For $optionName",
            onSuccess = {
                voteForOption(optionId, hashType, isAnonymous, onSuccess, onFailure)
            },
            onNoHardware = {
                voteForOption(optionId, hashType, isAnonymous, onSuccess, onFailure)
            }
        )
    }


    private fun voteForOption(
        optionId: String, hashType: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            try {
                val signatureId = UUID.randomUUID().toString()
                val answerHash = hash(
                    auth.currentUser!!.uid + currentState.poll!!.id + optionId + System.currentTimeMillis(),
                    hashType
                )

                // Call the repository function to vote for a budget option
                val voteOption = PollResponses(
                    answerId = UUID.randomUUID().toString(),
                    answerHash = answerHash.toString(),
                    userId = auth.currentUser!!.uid,
                    optionId = optionId,
//                            isAnonymous =
                    dateCreated = System.currentTimeMillis().toString()
                )

                val updatedResponses = currentState.poll.responses.toMutableList().apply {
                    add(voteOption)
                }

                repository.voteForPollOption(currentState.poll.id, updatedResponses)
                notificationRepository.sendPollVoteNotifications(currentState.poll, auth.currentUser!!.uid)
                onSuccess()
                // Update the UI state to reflect the voted option
                _uiState.value = currentState.copy(votedOptionId = optionId)
            } catch (e: Exception) {
                onFailure(e.message ?: "Signing petition failed")
                // If an error occurs, update the UI state with the error message
                _uiState.value = currentState.copy(error = e.message ?: "Vote failed")
            }
        }
    }


}

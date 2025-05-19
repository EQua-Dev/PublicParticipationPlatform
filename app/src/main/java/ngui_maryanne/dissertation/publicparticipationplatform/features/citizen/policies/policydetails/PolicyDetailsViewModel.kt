package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo.CommentRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.UUID
import javax.inject.Inject

// PolicyDetailsViewModel.kt
@HiltViewModel
class PolicyDetailsViewModel @Inject constructor(
    private val policyRepository: PolicyRepository,
    private val commentRepository: CommentRepository,
    private val notificationRepository: NotificationRepository,
    private val citizenRepository: CitizenRepository,
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences,
    private val pollsRepository: PollsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PolicyDetailsUiState())
    val uiState: StateFlow<PolicyDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PolicyDetailsEvent>()
    val events: SharedFlow<PolicyDetailsEvent> = _events

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

    private var commentsListener: ListenerRegistration? = null

    /*  init {
          viewModelScope.launch {
              commentsListener?.remove()
              commentsListener = commentRepository.getCommentsListener(policyId) { comments ->
                  _uiState.update { it.copy(comments = comments) }
              }
          }
      }*/

    fun handleAction(action: PolicyDetailsAction) {
        when (action) {
            is PolicyDetailsAction.LoadPolicy -> {
                loadPolicy(action.policyId)
                viewModelScope.launch {
                    setupCommentsListener(action.policyId)
                    _events.emit(PolicyDetailsEvent.SetupCommentsListener)
                }
            }

            PolicyDetailsAction.OnBackClicked -> viewModelScope.launch {
                _events.emit(PolicyDetailsEvent.NavigateBack)
            }

            is PolicyDetailsAction.OnPollClicked -> viewModelScope.launch {
                _events.emit(PolicyDetailsEvent.NavigateToPollDetails(action.pollId))
            }

            PolicyDetailsAction.ToggleDescriptionExpanded -> {
                _uiState.update { it.copy(isDescriptionExpanded = !it.isDescriptionExpanded) }
            }

            PolicyDetailsAction.ToggleTimelineExpanded -> {
                _uiState.update { it.copy(isTimelineExpanded = !it.isTimelineExpanded) }
            }

            is PolicyDetailsAction.OnCommentTextChanged -> {
                _uiState.update { it.copy(newCommentText = action.text) }
            }

            is PolicyDetailsAction.OnAnonymousToggled -> {
                _uiState.update { it.copy(isAnonymous = action.isAnonymous) }
            }

            PolicyDetailsAction.SubmitComment -> submitComment()
            PolicyDetailsAction.ToggleCommentsExpanded -> TODO()
            PolicyDetailsAction.TogglePollsExpanded -> TODO()
        }
    }

    private fun loadPolicy(policyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                policyRepository.getPolicy(policyId, _selectedLanguage.value).collect { policy ->
                    policy?.let {
                        _uiState.update { state ->
                            state.copy(
                                policy = policy,
                                isLoading = false,
                                error = null
                            )
                        }
                        loadPolls(policyId)
                    } ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Policy not found"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading policy"
                    )
                }
            }
        }
    }

    private fun loadPolls(policyId: String) {
        viewModelScope.launch {
            pollsRepository.getPollsForPolicy(policyId, _selectedLanguage.value).collect { polls ->
                _uiState.update { it.copy(polls = polls) }
            }
        }
    }

    private fun setupCommentsListener(policyId: String) {
        commentsListener?.remove()
        commentsListener = commentRepository.getCommentsListener(policyId, _selectedLanguage.value) { comments ->
            _uiState.update { it.copy(comments = comments) }
        }
    }

    fun getCitizenNameRealtime(userId: String, onResult: (String) -> Unit): ListenerRegistration {
        return citizenRepository.getCitizenRealtime(userId) { citizen ->
            val name = if (citizen != null) {
                "${citizen.firstName} ${citizen.lastName}"
            } else {
                "User ${userId.take(6)}"
            }
            onResult(name)
        }
    }

    private fun submitComment() {
        val commentText = _uiState.value.newCommentText
        if (commentText.isBlank()) return

        val policy = _uiState.value.policy ?: return
        if (policy.policyStatus != PolicyStatus.PUBLIC_CONSULTATION) return

        viewModelScope.launch {
            try {
                val comment = Comment(
                    comment = commentText,
                    userId = auth.currentUser!!.uid,
                    anonymous = _uiState.value.isAnonymous,
                    id = UUID.randomUUID().toString(),
                    dateCreated = System.currentTimeMillis().toString()
                )


                commentRepository.addComment(policy.id, comment)
                notificationRepository.sendPolicyCommentNotifications(policy.id, comment.userId)
                _uiState.update {
                    it.copy(
                        newCommentText = "",
                        isAnonymous = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Error submitting comment"
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        commentsListener?.remove()
    }
}


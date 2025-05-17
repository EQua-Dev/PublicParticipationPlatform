package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.di.TranslatorProvider
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.toTargetLang
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.translateTextWithMLKit
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CitizenPollsViewModel @Inject constructor(
    private val repository: PollsRepository,
    private val userPreferences: UserPreferences,
    private val translatorProvider: TranslatorProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenPollsUiState())
    val uiState: StateFlow<CitizenPollsUiState> = _uiState.asStateFlow()

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
                }

            observePolls()
        }
    }


    /* private val _events = Channel<CitizenPollsEvent>()
     val events = _events.receiveAsFlow()
 */

    private var listenerRegistration: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                _uiState.update { it.copy(currentUserRole = role ?: UserRole.CITIZEN) }
            }
        }
//        observePolls()
    }

    init {
        loadData()
    }


    private var pollsListener: ListenerRegistration? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observePolls() {
        // Clean up previous listener if needed
        pollsListener?.remove()

        viewModelScope.launch {
            try {
                val targetLang = _selectedLanguage.value.toTargetLang()
                Log.d("translatePollToLanguage", "observePolls: ${_selectedLanguage.value}")
                // Use a single collect operation to handle the Flow of polls
                repository.getAllPolls(_selectedLanguage.value).collect { rawPolls ->
                    Log.d("Citizen Polls ViewModel", "observePolls: received raw polls $rawPolls")
                    val translatedPolls = rawPolls.map { poll ->
                        translatePollToLanguage(poll, targetLang)
                    }

                    Log.d("TAG", "observePolls: $targetLang $translatedPolls")
                    // Process each poll to get policy information
                    val pollsWithPolicyName = rawPolls.mapNotNull { poll ->
                        try {
                            val policy = repository.getPolicySnapshot(poll.policyId)
                            PollWithPolicyName(
                                poll = poll,
                                policyName = policy?.policyTitle ?: "Unknown Policy",
                                policyStatus = policy?.policyStatus
                            )
                        } catch (e: Exception) {
                            Log.e(
                                "Citizen Polls ViewModel",
                                "Failed to get policy for poll ${poll.id}: ${e.message}"
                            )
                            null // Skip if policy fetch fails
                        }
                    }

                    // Apply current filters
                    val filtered = applyFilters(
                        pollsWithPolicyName,
                        _uiState.value.searchQuery,
                        _uiState.value.selectedStatus
                    )

                    // Update the UI state with the processed polls
                    _uiState.update {
                        it.copy(
                            polls = filtered,
                            allPolls = pollsWithPolicyName,
                            isLoading = false,
                            error = null,
                            lastUpdated = LocalDateTime.now()
                        )
                    }

                    Log.d("Citizen Polls ViewModel", "UI State updated with ${filtered.size} polls")
                }
            } catch (e: Exception) {
                Log.e("Citizen Polls ViewModel", "Error observing polls: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load polls: ${e.message}"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: CitizenPollsEvent) {
        when (event) {
            is CitizenPollsEvent.OnPollClicked -> {
                /* viewModelScope.launch {
                     _events.send(event)
                 }*/
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

    fun loadData() {
        viewModelScope.launch {
            pollsListener =
                repository.getAllPollsListener(_selectedLanguage.value) { polls ->
                    Log.d("Polls Viewmodelsss", "loadData: $polls")
//                    _state.value = _state.value.copy(polls = polls)
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
        pollsListener?.remove()
    }

    suspend fun translateText(text: String, sourceLang: String, targetLang: String): String {
        val translator = translatorProvider.getTranslator(sourceLang, targetLang)
        return suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { cont.resume(it) {} }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }

    suspend fun translatePollToLanguage(poll: Poll, targetLang: String): Poll {
        val sourceLang = if (targetLang == TranslateLanguage.ENGLISH) {
            TranslateLanguage.SWAHILI
        } else {
            TranslateLanguage.ENGLISH
        }

        Log.d("translatePollToLanguage", "$targetLang $poll")
        return poll.copy(
            pollQuestion = translateText(poll.pollQuestion, sourceLang, targetLang),
            pollOptions = poll.pollOptions.map { option ->
                option.copy(
                    optionText = translateTextWithMLKit(option.optionText, targetLang),
                    optionExplanation = translateTextWithMLKit(option.optionExplanation, targetLang)
                )
            }
        )
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.createpoll

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CreatePollViewModel @Inject constructor(
    private val pollRepository: PollsRepository,
    private val policyRepository: PolicyRepository,
    private val blockChainRepository: BlockChainRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePollState())
    val state: StateFlow<CreatePollState> = _state.asStateFlow()

    init {
        onEvent(CreatePollEvent.LoadPolicies)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: CreatePollEvent) {
        when (event) {
            is CreatePollEvent.QuestionChanged -> {
                _state.value = _state.value.copy(pollQuestion = event.text)
            }

            is CreatePollEvent.PolicySelected -> {
                _state.value = _state.value.copy(selectedPolicy = event.policy)
            }

            is CreatePollEvent.ExpiryDaysChanged -> {
                _state.value = _state.value.copy(expiryDays = event.days)
            }

            is CreatePollEvent.OptionChanged -> {
                val newOptions = _state.value.options.toMutableList()
                newOptions[event.index] = event.option
                _state.value = _state.value.copy(options = newOptions)
            }

            CreatePollEvent.AddOption -> {
                _state.value = _state.value.copy(
                    options = _state.value.options + PollOption()
                )
            }

            is CreatePollEvent.RemoveOption -> {
                val newOptions = _state.value.options.toMutableList()
                newOptions.removeAt(event.index)
                _state.value = _state.value.copy(options = newOptions)
            }

            CreatePollEvent.Submit -> {
                createPoll()
            }

            CreatePollEvent.LoadPolicies -> loadPolicies()
            CreatePollEvent.DismissError -> {
                _state.value = _state.value.copy(error = null)
            }
        }
    }

    private fun loadPolicies() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val policies = policyRepository.getPoliciesBeforePublicOpinion()
                _state.value = _state.value.copy(
                    policies = policies,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load policies: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPoll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                Log.d("CPVM", "createPoll: creating poll...")
                val currentState = _state.value
                if (currentState.selectedPolicy?.policyStatus != PolicyStatus.PUBLIC_CONSULTATION
                    || currentState.selectedPolicy?.policyStatus != PolicyStatus.MINISTERIAL_APPROVAL
                    || currentState.selectedPolicy?.policyStatus != PolicyStatus.INTERNAL_REVIEW
                ) {
                    Log.d(
                        "CPVM",
                        "createPoll: Polls can only be created for policies in Public Consultation phase"
                    )
                    _state.value =
                        _state.value.copy(error = "Polls can only be created for policies in Public Consultation phase")
                    throw Exception("Polls can only be created for policies in Public Consultation phase")

                }

                val poll = Poll(
                    id = UUID.randomUUID().toString(),
                    pollQuestion = currentState.pollQuestion,
                    policyId = currentState.selectedPolicy.id,
                    pollExpiry = LocalDate.now()
                        .plusDays(currentState.expiryDays.toLong())
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli().toString(),
                    pollOptions = currentState.options,
                    createdBy = auth.currentUser?.uid ?: "",
                    dateCreated = LocalDateTime.now().toString()
                )

                pollRepository.createPoll(poll)
                blockChainRepository.createBlockchainTransaction(
                    TransactionTypes.CREATE_POLL
                )
                _state.value = _state.value.copy(
                    isLoading = false,
                    createSuccess = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to create poll",
                    isLoading = false
                )
            }
        }
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.commentrepo.CommentRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo.PolicyRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.pollsrepo.PollsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import javax.inject.Inject

@HiltViewModel
class OfficialPolicyDetailsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val policyRepository: PolicyRepository,
    private val pollRepository: PollsRepository,
    private val commentRepository: CommentRepository,
    private val officialRepository: OfficialsRepository,
    private val storageRepository: StorageRepository,
    private val blockChainRepository: BlockChainRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OfficialPolicyDetailsState())
    val state: StateFlow<OfficialPolicyDetailsState> = _state.asStateFlow()

    private var policyListener: ListenerRegistration? = null
    private var pollsListener: ListenerRegistration? = null
    private var commentsListener: ListenerRegistration? = null

    fun onEvent(event: OfficialPolicyDetailsEvent) {
        when (event) {
            is OfficialPolicyDetailsEvent.LoadData -> loadData(event.policyId)
            is OfficialPolicyDetailsEvent.UpdateStage -> updateStage(event.newStage)
            OfficialPolicyDetailsEvent.ShowStageDialog -> showStageDialog()
            OfficialPolicyDetailsEvent.DismissStageDialog -> dismissStageDialog()
            OfficialPolicyDetailsEvent.DismissError -> dismissError()
            is OfficialPolicyDetailsEvent.CreatePoll -> createPoll(event.policyId)

            is OfficialPolicyDetailsEvent.UpdatePolicy -> updatePolicy(event.name, event.imageUrl, event.otherDetails)
            OfficialPolicyDetailsEvent.DeletePolicy -> deletePolicy()
        }
    }

    private fun loadData(policyId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val official = officialRepository.getCurrentOfficial()
                val canCreatePoll = official.permissions.contains("create_poll")
                val canUpdateStage = official.permissions.contains("update_policy_stage")

                _state.value = _state.value.copy(
                    canCreatePoll = canCreatePoll,
                    canUpdateStage = canUpdateStage
                )

                // Setup realtime listeners
                setupListeners(policyId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to load data: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    private fun setupListeners(policyId: String) {
//        val policyId = _state.value.policy?.id ?: return

        policyListener = policyRepository.getPolicyListener(policyId) { policy ->
            _state.value = _state.value.copy(
                policy = policy,
                currentStage = policy?.policyStatus ?: PolicyStatus.DRAFT,
                isLoading = false
            )
        }

        pollsListener = pollRepository.getPollsListener(policyId) { polls ->
            _state.value = _state.value.copy(polls = polls)
        }

        commentsListener = commentRepository.getCommentsListener(policyId) { comments ->
            _state.value = _state.value.copy(comments = comments)
        }
    }

    private fun updateStage(newStage: PolicyStatus) {
        viewModelScope.launch {
            try {
                _state.value.policy?.id?.let { policyId ->
                    policyRepository.updatePolicyStage(policyId, newStage)
                    blockChainRepository.createBlockchainTransaction(
                        TransactionTypes.UPDATE_POLICY_STATUS
                    )
                    _state.value = _state.value.copy(
                        showStageUpdateDialog = false,
                        currentStage = newStage
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update stage: ${e.message}",
                    showStageUpdateDialog = false
                )
            }
        }
    }

    private fun updatePolicy(name: String, imageUrl: String, otherDetails: Map<String, Any?>) {
        viewModelScope.launch {
            try {
                val policyId = _state.value.policy?.id ?: return@launch
                val storageImageUrl = imageUrl.let { uri ->
                    storageRepository.uploadPolicyImage(uri.toUri())
                } ?: ""

                policyRepository.updatePolicy(policyId, name, storageImageUrl, otherDetails)
                _state.value = _state.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to update policy: ${e.message}"
                )
            }
        }
    }

    private fun deletePolicy() {
        viewModelScope.launch {
            try {
                val policyId = _state.value.policy?.id ?: return@launch
                policyRepository.deletePolicy(policyId)
                _state.value = _state.value.copy(
                    error = null
                )
                // Maybe navigate back after deleting? Trigger a nav action here if you want
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete policy: ${e.message}"
                )
            }
        }
    }


    private fun showStageDialog() {
        _state.value = _state.value.copy(showStageUpdateDialog = true)
    }

    private fun dismissStageDialog() {
        _state.value = _state.value.copy(showStageUpdateDialog = false)
    }

    private fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun createPoll(policyId: String) {
        // Navigation handled in UI layer
    }

    override fun onCleared() {
        super.onCleared()
        policyListener?.remove()
        pollsListener?.remove()
        commentsListener?.remove()
    }
}
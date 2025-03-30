package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.createpolicy

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.StatusChange
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.BlockChainRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.PolicyRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.StorageRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreatePolicyViewModel @Inject constructor(
    private val policyRepository: PolicyRepository,
    private val storageRepository: StorageRepository,
    private val blockChainRepository: BlockChainRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(CreatePolicyState())
    val state: StateFlow<CreatePolicyState> = _state.asStateFlow()

    fun onEvent(event: CreatePolicyEvent) {
        when (event) {
            is CreatePolicyEvent.PolicyNameChanged -> {
                _state.value = _state.value.copy(policyName = event.value)
            }

            is CreatePolicyEvent.PolicyTitleChanged -> {
                _state.value = _state.value.copy(policyTitle = event.value)
            }

            is CreatePolicyEvent.PolicySectorChanged -> {
                _state.value = _state.value.copy(policySector = event.value)
            }

            is CreatePolicyEvent.PolicyDescriptionChanged -> {
                _state.value = _state.value.copy(policyDescription = event.value)
            }

            is CreatePolicyEvent.CoverImageSelected -> {
                _state.value = _state.value.copy(coverImageUri = event.uri)
            }

            CreatePolicyEvent.Submit -> {
                Log.d("CPVM", "onEvent: submit")
                createPolicy()
            }
            CreatePolicyEvent.DismissError -> {
                _state.value = _state.value.copy(error = null)
            }

            CreatePolicyEvent.DismissSuccess -> {
                _state.value = _state.value.copy(isSuccess = false)
            }

            is CreatePolicyEvent.StatusChanged -> {
                _state.value = _state.value.copy(selectedStatus = event.status)
            }
        }
    }

    private fun createPolicy() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Upload image first if exists
                val imageUrl = _state.value.coverImageUri?.let { uri ->
                    storageRepository.uploadPolicyImage(uri)
                } ?: ""

                val policy = Policy(
                    id = UUID.randomUUID().toString(),
                    policyName = _state.value.policyName,
                    policyTitle = _state.value.policyTitle,
                    policySector = _state.value.policySector,
                    policyDescription = _state.value.policyDescription,
                    policyCoverImage = imageUrl,
                    policyStatus = _state.value.selectedStatus,
                    dateCreated = System.currentTimeMillis().toString(),
                    createdBy = auth.currentUser!!.uid,
                    statusHistory = listOf(
                        StatusChange(
                            status = _state.value.selectedStatus,
                            changedAt = System.currentTimeMillis().toString(),
                            changedBy = auth.currentUser!!.uid,
                            notes = "Initial status"
                        )
                    )
                )

                Log.d("CPVM", "createPolicy: $policy")
                policyRepository.createPolicy(policy)
                blockChainRepository.createBlockchainTransaction(
                    auth.currentUser!!.uid,
                    TransactionTypes.CREATE_POLICY
                )
                _state.value = _state.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to create policy",
                    isLoading = false
                )
            }
        }
    }
}
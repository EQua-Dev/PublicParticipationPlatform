package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.TransactionTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.blockchainrepo.BlockChainRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import javax.inject.Inject

@HiltViewModel
class OfficialDetailViewModel @Inject constructor(
    private val repository: OfficialsRepository,
    private val blockChainRepository: BlockChainRepository,
    private val storageRepository: StorageRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf(OfficialDetailUiState())
    val uiState: State<OfficialDetailUiState> = _uiState

    private val _eventFlow = MutableSharedFlow<OfficialDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var listenerRegistration: ListenerRegistration? = null


    fun onEvent(event: OfficialDetailEvent) {
        when (event) {
            is OfficialDetailEvent.LoadOfficial -> {
                getOfficialDetailsRealtime(event.id)
            }

            is OfficialDetailEvent.UpdateOfficial -> updateOfficial(event.updatedOfficial)
            OfficialDetailEvent.DeactivateOfficial -> deactivateOfficial()
            is OfficialDetailEvent.OfficialUpdated -> {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }

            is OfficialDetailEvent.OfficialDeactivated -> {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }

            else -> {

            }
        }
    }


    private fun getOfficialDetailsRealtime(officialId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            repository.getOfficialByIdRealtime(officialId).collect { official ->
                _uiState.value = OfficialDetailUiState(
                    official = official

                )

            }
        }
        /* listenerRegistration = FirebaseFirestore.getInstance()
             .collection("officials")
             .document(officialId)
             .addSnapshotListener { snapshot, error ->
                 if (error != null) {
                     _uiState.value = OfficialDetailUiState(error = error.message)
                     return@addSnapshotListener
                 }

                 val official = snapshot?.toObject(Official::class.java)?.copy(id = snapshot.id)
                 _uiState.value = OfficialDetailUiState(official = official)
             }*/
    }

    private fun updateOfficial(updatedOfficial: Official) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val existingImageUrl = _uiState.value.official?.profileImageUrl
                val newImageUri = updatedOfficial.profileImageUrl

                val uploadedImageUrl = if (newImageUri != null && newImageUri != existingImageUrl) {
                    storageRepository.uploadProfileImage(
                        updatedOfficial.id,
                        newImageUri.toUri()
                    )
                } else {
                    existingImageUrl
                }

                val officialToUpdate = updatedOfficial.copy(profileImageUrl = uploadedImageUrl)

                repository.updateOfficial(officialToUpdate)
                blockChainRepository.createBlockchainTransaction(TransactionTypes.UPDATE_OFFICIAL)
                _eventFlow.emit(OfficialDetailEvent.OfficialUpdated)
            } catch (e: Exception) {
                _eventFlow.emit(OfficialDetailEvent.ShowError(e.message ?: "Unknown error"))
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }

    }

    private fun deactivateOfficial() {
        val official = _uiState.value.official ?: return
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val deactivatedOfficial = official.copy(permissions = emptyList(), active = false)
                updateOfficial(deactivatedOfficial)
                blockChainRepository.createBlockchainTransaction(TransactionTypes.DEACTIVATE_OFFICIAL)
                _eventFlow.emit(OfficialDetailEvent.OfficialDeactivated)
            } catch (e: Exception) {
                _eventFlow.emit(OfficialDetailEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import javax.inject.Inject

@HiltViewModel
class PetitionDetailsViewModel @Inject constructor(
    private val repository: PetitionRepository
) : ViewModel() {
    private val _state = mutableStateOf(PetitionDetailsState())
    val state: State<PetitionDetailsState> = _state

    fun onEvent(event: PetitionDetailsEvent) {
        when (event) {
            is PetitionDetailsEvent.LoadPetition -> {
                getPetitionRealtime(event.petitionId)
             /*   viewModelScope.launch {
                    _state.value = _state.value.copy(isLoading = true)
                    try {
                        val petition = repository.getPetitionById(event.petitionId)
                        _state.value = _state.value.copy(petition = petition, isLoading = false)
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(error = e.message, isLoading = false)
                    }
                }*/
            }
            PetitionDetailsEvent.SignPetition -> {
               /* val petition = _state.value.petition ?: return
                val userId = _state.value.currentUserId
                if (petition.createdBy == userId || petition.signatures.contains(userId)) return

                viewModelScope.launch {
                    try {
                        repository.signPetition(petition.id, userId)
                        val updatedPetition = petition.copy(signatures = petition.signatures + userId)
                        _state.value = _state.value.copy(petition = updatedPetition, hasSigned = true)
                    } catch (e: Exception) {
                        _state.value = _state.value.copy(error = e.message)
                    }
                }*/
            }
        }
    }

    fun getPetitionRealtime(id: String) {
        viewModelScope.launch {
            repository.getPetitionById(id).collect { petition ->
                _state.value = _state.value.copy(
                    petition = petition,
                    isLoading = false
                )
            }
        }
    }
}
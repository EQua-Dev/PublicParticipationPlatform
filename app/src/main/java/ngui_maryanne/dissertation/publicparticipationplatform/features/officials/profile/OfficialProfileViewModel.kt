package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import javax.inject.Inject

@HiltViewModel
class OfficialProfileViewModel @Inject constructor(
    private val repository: OfficialsRepository,
    private val storageRepository: StorageRepository,
    private val citizenRepository: CitizenRepository,
) : ViewModel() {

    private val _state = mutableStateOf(OfficialProfileState())
    val state: State<OfficialProfileState> = _state

    init {
        onEvent(OfficialProfileEvent.LoadProfile)
    }

    fun onEvent(event: OfficialProfileEvent) {
        when (event) {
            OfficialProfileEvent.LoadProfile -> loadProfile()
            OfficialProfileEvent.ToggleEditMode -> toggleEditMode()
            is OfficialProfileEvent.PhoneNumberChanged -> updatePhoneNumber(event.newNumber)
            is OfficialProfileEvent.ProfileImageSelected -> selectProfileImage(event.uri)
            OfficialProfileEvent.SaveProfile -> saveProfile()
            OfficialProfileEvent.DismissSuccess -> dismissSuccess()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val official = repository.getCurrentOfficial()
                _state.value = _state.value.copy(
                    official = official,
                    editedPhoneNumber = official.phoneNumber,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun toggleEditMode() {
        _state.value = _state.value.copy(
            isEditing = !_state.value.isEditing,
            updateSuccess = false
        )
    }

    private fun updatePhoneNumber(newNumber: String) {
        _state.value = _state.value.copy(editedPhoneNumber = newNumber)
    }

    private fun selectProfileImage(uri: Uri) {
        _state.value = _state.value.copy(profileImageUri = uri)
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val currentOfficial = _state.value.official
                var imageUrl = currentOfficial.profileImageUrl

                // Upload new image if selected
                _state.value.profileImageUri?.let { uri ->
                    imageUrl = storageRepository.uploadProfileImage(currentOfficial.id, uri)
                }

                val updatedOfficial = currentOfficial.copy(
                    phoneNumber = _state.value.editedPhoneNumber,
                    profileImageUrl = imageUrl
                )

                repository.updateOfficial(updatedOfficial)
                _state.value = _state.value.copy(
                    official = updatedOfficial,
                    isEditing = false,
                    isLoading = false,
                    updateSuccess = true,
                    profileImageUri = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun dismissSuccess() {
        _state.value = _state.value.copy(updateSuccess = false)
    }
}
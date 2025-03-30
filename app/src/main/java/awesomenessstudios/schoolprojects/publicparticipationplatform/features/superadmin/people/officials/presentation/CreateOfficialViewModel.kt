package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOfficialViewModel @Inject constructor(
    private val officialsRepository: OfficialsRepository
) : ViewModel() {

    private val _state = mutableStateOf(CreateOfficialUiState())
    val uiState: State<CreateOfficialUiState> = _state


    fun onEvent(event: CreateOfficialUiEvent) {
        when (event) {
            is CreateOfficialUiEvent.UpdateFirstName -> {
                Log.d("COVM", "onEvent: ${event.value}")
                _state.value =
                    _state.value.copy(firstName = event.value)
            }

            is CreateOfficialUiEvent.UpdateLastName -> _state.value =
                _state.value.copy(lastName = event.value)

            is CreateOfficialUiEvent.UpdateEmail -> _state.value = _state.value.copy(email = event.value)
            is CreateOfficialUiEvent.UpdatePhoneNumber -> _state.value =
                _state.value.copy(phoneNumber = event.value)

            is CreateOfficialUiEvent.TogglePermission -> {
                val updatedPermissions = _state.value.permissions.toMutableSet()
                if (updatedPermissions.contains(event.permission)) {
                    updatedPermissions.remove(event.permission)
                } else {
                    updatedPermissions.add(event.permission)
                }
                _state.value = _state.value.copy(permissions = updatedPermissions)
            }

            is CreateOfficialUiEvent.UpdateProfileImage -> _state.value =
                _state.value.copy(profileImageUri = event.uri)

            is CreateOfficialUiEvent.CreateOfficial -> createOfficial()
        }
    }

    private fun createOfficial() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val official = Official(
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                email = _state.value.email,
                phoneNumber = _state.value.phoneNumber,
                permissions = _state.value.permissions.toList()
            )

            officialsRepository.createOfficial(official, _state.value.profileImageUri) { success, error ->
                _state.value = if (success) {
                    CreateOfficialUiState( // Reset all fields
                        successMessage = "Official created!"
                    )
                } else {
                    _state.value.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            }
        }
    }
}

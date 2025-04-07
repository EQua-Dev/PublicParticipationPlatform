package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.NationalCitizen
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.nationaldbrepo.NationalDBRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCitizenViewModel @Inject constructor(
    private val repository: NationalDBRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = mutableStateOf(AddCitizenUiState())
    val state: State<AddCitizenUiState> = _state


    fun onEvent(event: AddCitizenEvent) {
        when (event) {
            is AddCitizenEvent.EnteredName -> _state.value = _state.value.copy(name = event.value)
            is AddCitizenEvent.EnteredPhone -> _state.value =
                _state.value.copy(phoneNumber = event.value)

            is AddCitizenEvent.EnteredNationalId -> _state.value =
                _state.value.copy(nationalId = event.value)

            is AddCitizenEvent.SelectedProfileImage -> _state.value =
                _state.value.copy(profileImageUri = event.uri)

            is AddCitizenEvent.Submit -> addCitizen()
        }
    }

    private fun addCitizen() {
        _state.value = _state.value.copy(isLoading = true)
        val citizen = NationalCitizen(
            name = _state.value.name,
            phoneNumber = _state.value.phoneNumber,
            nationalId = _state.value.nationalId,
            createdBy = auth.currentUser!!.uid
        )

        repository.addCitizen(citizen, _state.value.profileImageUri) { success, error ->
            _state.value =
                if (success) AddCitizenUiState(successMessage = "Citizen added!") else _state.value.copy(
                    errorMessage = error
                )
        }
    }
}

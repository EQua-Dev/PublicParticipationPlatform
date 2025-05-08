package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LanguageManager
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class CitizenProfileViewModel @Inject constructor(
    private val citizenRepo: CitizenRepository,
    private val storageRepo: StorageRepository,
    private val auth: FirebaseAuth,
    private val preferencesRepo: UserPreferences,
    @ApplicationContext private val context: Context

) : ViewModel() {

    private val _state = mutableStateOf(CitizenProfileState())
    val state: State<CitizenProfileState> = _state

    private val appLocaleManager = LanguageManager()

    init {
        onEvent(CitizenProfileEvent.LoadProfile)
    }

    fun onEvent(event: CitizenProfileEvent) {
        when (event) {
            is CitizenProfileEvent.LoadProfile -> loadProfile()
            is CitizenProfileEvent.ToggleEditMode -> toggleEditMode()
            is CitizenProfileEvent.FirstNameChanged -> _state.value =
                _state.value.copy(firstName = event.value)

            is CitizenProfileEvent.LastNameChanged -> _state.value =
                _state.value.copy(lastName = event.value)

            is CitizenProfileEvent.PhoneNumberChanged -> _state.value =
                _state.value.copy(phoneNumber = event.value)

            is CitizenProfileEvent.OccupationChanged -> _state.value =
                _state.value.copy(occupation = event.value)

            is CitizenProfileEvent.CountyOfResidenceChanged -> _state.value =
                _state.value.copy(countyOfResidence = event.value)

            is CitizenProfileEvent.LanguageChanged -> changeLanguage(event.language)
            is CitizenProfileEvent.ProfileImageSelected -> _state.value =
                _state.value.copy(profileImageUri = event.uri)

            is CitizenProfileEvent.SaveProfile -> saveProfile()
            is CitizenProfileEvent.DismissSuccess -> _state.value =
                _state.value.copy(updateSuccess = false)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val lang = preferencesRepo.languageFlow.first()
                citizenRepo.getCitizenRealtime(auth.currentUser!!.uid, onUpdate = { citizen ->
                    if (citizen != null) {
                        _state.value = _state.value.copy(
                            citizen = citizen,
                            firstName = citizen.firstName,
                            lastName = citizen.lastName,
                            phoneNumber = citizen.phoneNumber,
                            occupation = citizen.occupation,
                            countyOfResidence = citizen.countyOfResidence,
                            selectedLanguage = lang,
                            isLoading = false
                        )
                    }
                })


            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    private fun toggleEditMode() {
        _state.value = _state.value.copy(isEditing = !_state.value.isEditing, updateSuccess = false)
    }

    private fun changeLanguage(lang: ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage) {
     /*   viewModelScope.launch {
            appLocaleManager.setLocale(context, lang.locale.language)

            preferencesRepo.setLanguage(lang)
            _state.value = _state.value.copy(selectedLanguage = lang)
        }*/
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val current = _state.value.citizen!!
                var imageUrl = current.profileImage

                _state.value.profileImageUri?.let { uri ->
                    imageUrl = storageRepo.uploadProfileImage(current.id, uri)
                }

                val updated = current.copy(
                    firstName = _state.value.firstName,
                    lastName = _state.value.lastName,
                    phoneNumber = _state.value.phoneNumber,
                    occupation = _state.value.occupation,
                    countyOfResidence = _state.value.countyOfResidence,
                    profileImage = imageUrl
                )

                citizenRepo.updateCitizenDetails(
                    updated.id, mapOf(
                        "firstName" to updated.firstName,
                        "lastName" to updated.lastName,
                        "phoneNumber" to updated.phoneNumber,
                        "occupation" to updated.occupation,
                        "countyOfResidence" to updated.countyOfResidence,
                        "profileImage" to updated.profileImage
                    )
                )

                _state.value = _state.value.copy(
                    citizen = updated,
                    isEditing = false,
                    isLoading = false,
                    updateSuccess = true,
                    profileImageUri = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}

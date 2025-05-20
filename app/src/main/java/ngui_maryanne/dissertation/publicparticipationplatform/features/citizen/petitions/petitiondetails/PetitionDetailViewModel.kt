package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.time.LocalDateTime
import java.util.Objects.hash
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PetitionDetailsViewModel @Inject constructor(
    private val repository: PetitionRepository,
    private val notificationRepository: NotificationRepository,
    private val storageRepository: StorageRepository,
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _state = MutableStateFlow(PetitionDetailsState())
    val state: StateFlow<PetitionDetailsState> = _state.asStateFlow()

    private val _events = Channel<PetitionDetailsResult>()
    val events = _events.receiveAsFlow()

    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                }
        }
    }

    private var petitionListener: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                _state.update { it.copy(currentUserRole = role ?: UserRole.CITIZEN) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: PetitionDetailsEvent) {
        when (event) {
            is PetitionDetailsEvent.LoadPetition -> {
                viewModelScope.launch {
                    userPreferences.languageFlow
                        .distinctUntilChanged()
                        .collect { lang ->
                            Log.d("TAG", "selected language: $lang")
                            _selectedLanguage.value = lang
                            loadPetition(event.petitionId, lang)
                        }
                }


            }
            is PetitionDetailsEvent.SignPetition -> signPetition(event.activity, event.isAnonymous)
            is PetitionDetailsEvent.UpdatePetition -> updatePetition(event.title, event.description, event.coverImage, event.requestGoals)
            PetitionDetailsEvent.DeletePetition -> deletePetition()
            PetitionDetailsEvent.Retry -> retry()
            PetitionDetailsEvent.ClearError -> clearError()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPetition(petitionId: String, lang: AppLanguage) {
        petitionListener?.remove()
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.getPetitionById(petitionId, lang).collect { snapshot ->
                    val petition = snapshot ?: throw Exception("Petition not found")
                    val currentUserId = auth.currentUser?.uid.orEmpty()
                    val hasSigned = petition.signatures.any { it.userId == currentUserId }

                    _state.update {
                        it.copy(
                            petition = petition,
                            currentUserId = currentUserId,
                            hasSigned = hasSigned,
                            isLoading = false,
                            error = null,
                            lastUpdated = LocalDateTime.now()
                        )
                    }

                    _events.send(PetitionDetailsResult.PetitionLoaded(petition))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load petition"
                    )
                }
                _events.send(PetitionDetailsResult.Error(e.message ?: "Failed to load petition"))

            }}}

    @RequiresApi(Build.VERSION_CODES.P)
    private fun signPetition(activity: FragmentActivity, isAnonymous: Boolean) {
        val currentState = _state.value
        val petition = currentState.petition ?: return
        val userId = currentState.currentUserId

        if (petition.createdBy == userId || currentState.hasSigned) return

        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Petition Signing",
            onSuccess = { processSignature(petition, userId, isAnonymous) },
            onNoHardware = { processSignature(petition, userId, isAnonymous) },
            /*onFailure = { error ->
                _events.send(PetitionDetailsResult.Error(error))
            }*/
        )
    }

    private fun processSignature(petition: Petition, userId: String, isAnonymous: Boolean) {
        viewModelScope.launch {
            try {
                val signatureId = UUID.randomUUID().toString()
                val signatureHash = hash(userId + petition.id + System.currentTimeMillis(), "SHA-256")

                val newSignature = Signature(
                    id = signatureId,
                    hash = signatureHash.toString(),
                    userId = userId,
                    dateCreated = System.currentTimeMillis().toString(),
                    isAnonymous = isAnonymous
                )

                val updatedSignatures = petition.signatures.toMutableList().apply {
                    add(newSignature)
                }

                repository.signPetition(petition.id, updatedSignatures)
                notificationRepository.sendPetitionSignNotifications(petition, userId)

                _events.send(PetitionDetailsResult.PetitionSigned(true))
            } catch (e: Exception) {
                _events.send(PetitionDetailsResult.Error(e.message ?: "Failed to sign petition"))
            }
        }
    }

    private fun updatePetition(title: String, description: String, coverImage: String, requestGoals: List<String>) {
        viewModelScope.launch {
            try {
                val petitionId = _state.value.petition?.id ?: return@launch

                val otherDetails = mapOf("description" to description, "requestGoals" to requestGoals)
                val updatedCoverImage = storageRepository.uploadImage("petition_images/", coverImage.toUri())
                Log.d("TAG", "updatePetition: $updatedCoverImage")
                repository.updatePetition(
                    petitionId = petitionId,
                    name = title,
                    imageUrl = updatedCoverImage,
                    otherDetails = otherDetails,
                )
                _events.send(PetitionDetailsResult.PetitionUpdated(true))
            } catch (e: Exception) {
                _events.send(PetitionDetailsResult.Error("Failed to update petition: ${e.message}"))
            }
        }
    }

    private fun deletePetition() {
        viewModelScope.launch {
            try {
                val petitionId = _state.value.petition?.id ?: return@launch
                repository.deletePetition(petitionId)
                _events.send(PetitionDetailsResult.PetitionDeleted(true))
            } catch (e: Exception) {
                _events.send(PetitionDetailsResult.Error("Failed to delete petition: ${e.message}"))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun retry() {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    _state.value.petition?.id?.let { loadPetition(it, lang) }
                }
        }

    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        petitionListener?.remove()
    }
}
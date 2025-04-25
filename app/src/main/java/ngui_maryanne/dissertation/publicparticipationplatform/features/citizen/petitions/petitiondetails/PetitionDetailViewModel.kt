package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Signature
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.Objects.hash
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PetitionDetailsViewModel @Inject constructor(
    private val repository: PetitionRepository,
    private val userPreferences: UserPreferences,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : ViewModel() {
    private val _state = mutableStateOf(PetitionDetailsState())
    var state: State<PetitionDetailsState> = _state

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                if (role != null) {
                    _state.value = _state.value.copy(
                        currentUserRole = role.name
                    )
                }
            }
        }
    }

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
            /*    verifyAndSignPetition(userId = _state.value.currentUserId, petition = _state.value.petition!!, hashType = "SHA-256", )
                HelpMe.promptBiometric(
                    activity = activity,
                    title = "Authorize Transaction",
                    onSuccess = {
                        fundWallet(wallet, onSuccess, onFailure)
                    },
                    onNoHardware = {
                        fundWallet(wallet, onSuccess, onFailure)
                    }
                )
               val petition = _state.value.petition ?: return
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
                val hasSigned = petition?.signatures?.any { it.userId == auth.currentUser!!.uid } == true

                _state.value = _state.value.copy(
                    petition = petition,
                    isLoading = false,
                    currentUserId = auth.currentUser!!.uid,
                    hasSigned = hasSigned
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyAndSignPetition(
        activity: FragmentActivity,
        petition: Petition,
        userId: String,
        hashType: String,
//        answer1: String,
//        answer2: String,
//        securityQuestion1: String,
//        securityQuestion2: String,
//        storedSecurityHash: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
 /*       val hash1 = hash(securityQuestion1 + answer1, hashType)
        val hash2 = hash(securityQuestion2 + answer2, hashType)
        val combinedHash = hash(hash1 + hash2, hashType)

        if (combinedHash != storedSecurityHash) {
            onFailure("Security answers don't match")
            return
        }
*/
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Petition Signing",
            onSuccess = {
                signPetition(petition, userId, hashType, isAnonymous, onSuccess, onFailure)
            },
            onNoHardware = {
                signPetition(petition, userId, hashType, isAnonymous, onSuccess, onFailure)
            }
        )
    }

    private fun signPetition(
        petition: Petition,
        userId: String,
        hashType: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val signatureId = UUID.randomUUID().toString()
                val signatureHash = hash(userId + petition.id + System.currentTimeMillis(), hashType)

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
                onSuccess()
            }catch (e: Exception) {
                onFailure(e.message ?: "Signing petition failed")
            }
        }
    }

    fun updateError(error: String){
        viewModelScope.launch {
            _state.value = _state.value.copy(
                error = error,
                isLoading = false
            )
        }
    }

}
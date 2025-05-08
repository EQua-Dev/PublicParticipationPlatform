package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation.CitizenPetitionsUiState
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.CreatePolicyEvent
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PetitionViewModel @Inject constructor(
    private val repository: PetitionRepository,
    private val announcementRepository: AnnouncementRepository,
    private val storageRepo: StorageRepository
) : ViewModel() {

    private val _newPetitionState = mutableStateOf(NewPetitionState())
    val newPetitionState: State<NewPetitionState> = _newPetitionState

    /*

        var newPetitionState by mutableStateOf(NewPetitionState())
            private set
    */

    fun onNewPetitionEvent(event: NewPetitionEvent) {
        when (event) {
            is NewPetitionEvent.OnSectorChanged ->
                _newPetitionState.value = _newPetitionState.value.copy(sector = event.value)

            is NewPetitionEvent.OnTitleChanged ->
                _newPetitionState.value = _newPetitionState.value.copy(title = event.value)

            is NewPetitionEvent.OnDescriptionChanged ->
                _newPetitionState.value = _newPetitionState.value.copy(description = event.value)

            is NewPetitionEvent.OnCountyChanged ->
                _newPetitionState.value = _newPetitionState.value.copy(county = event.value)

            is NewPetitionEvent.CoverImageSelected -> {
                _newPetitionState.value = _newPetitionState.value.copy(coverImageUri = event.uri)
            }

            is NewPetitionEvent.OnRequestGoalChanged -> {
                val updated = _newPetitionState.value.requestGoals.toMutableList()
                updated[event.index] = event.value
                _newPetitionState.value = _newPetitionState.value.copy(requestGoals = updated)
            }

            is NewPetitionEvent.OnAddRequestGoal ->
                _newPetitionState.value = _newPetitionState.value.copy(
                    requestGoals = _newPetitionState.value.requestGoals + ""
                )

            is NewPetitionEvent.OnSupportingReasonChanged -> {
                val updated = _newPetitionState.value.supportingReasons.toMutableList()
                updated[event.index] = event.value
                _newPetitionState.value = _newPetitionState.value.copy(supportingReasons = updated)
            }

            is NewPetitionEvent.OnAddSupportingReason ->
                _newPetitionState.value = _newPetitionState.value.copy(
                    supportingReasons = _newPetitionState.value.supportingReasons + ""
                )

            is NewPetitionEvent.OnTargetSignatureChanged -> {
                val newCount =
                    (_newPetitionState.value.targetSignatures + event.diff).coerceAtLeast(200)
                _newPetitionState.value = _newPetitionState.value.copy(targetSignatures = newCount)
            }

            is NewPetitionEvent.OnTargetSignatureManuallyChanged -> {
                _newPetitionState.value =
                    _newPetitionState.value.copy(targetSignatures = event.newValue)
            }
        }
    }

    fun submitNewPetition(userId: String) {
        viewModelScope.launch {
            try {
                _newPetitionState.value = _newPetitionState.value.copy(isLoading = true)

                val imageUrl = _newPetitionState.value.coverImageUri?.let {
                    storageRepo.uploadImage("petitions/${UUID.randomUUID()}.jpg", it)
                } ?: ""

                val petition = Petition(
                    id = UUID.randomUUID().toString(),
                    hash = UUID.randomUUID().toString(),
                    petitionNo = "PET-${System.currentTimeMillis()}",
                    title = _newPetitionState.value.title,
                    description = _newPetitionState.value.description,
                    coverImage = imageUrl,
                    county = _newPetitionState.value.county,
                    sector = _newPetitionState.value.sector,
                    requestGoals = _newPetitionState.value.requestGoals,
                    signatureGoal = _newPetitionState.value.targetSignatures.toString(),
                    createdBy = userId,
                    expiryDate = System.currentTimeMillis().plus(30 * 24 * 60 * 60 * 1000L)
                        .toString(), // 30 days
                    signatures = listOf()
                )

                repository.createPetition(petition)
                val announcement = Announcement(
                    id = UUID.randomUUID().toString(),
                    createdBy = petition.createdBy,
                    createdAt = System.currentTimeMillis().toString(),
                    type = NotificationTypes.PETITION,
                    typeId = petition.id,
                    title = "New Petition",
                    description = "A new petition: ${petition.title} has been created",
                )

                announcementRepository.addAnnouncement(announcement, announcement.type)
                _newPetitionState.value = NewPetitionState() // reset after success

            } catch (e: Exception) {
                _newPetitionState.value = _newPetitionState.value.copy(error = e.message)
            } finally {
                _newPetitionState.value = _newPetitionState.value.copy(isLoading = false)
            }
        }
    }

}

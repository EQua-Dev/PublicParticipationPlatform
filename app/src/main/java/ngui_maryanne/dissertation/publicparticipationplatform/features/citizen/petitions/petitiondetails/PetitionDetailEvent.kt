package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import androidx.fragment.app.FragmentActivity
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.OfficialPolicyDetailsEvent

sealed class PetitionDetailsEvent {
    data class LoadPetition(val petitionId: String) : PetitionDetailsEvent()
    data class SignPetition(
        val activity: FragmentActivity,
        val isAnonymous: Boolean
    ) : PetitionDetailsEvent()
    data class UpdatePetition(
        val title: String,
        val description: String,
        val coverImage: String,
        val requestGoals: List<String>
    ) : PetitionDetailsEvent()
    object DeletePetition : PetitionDetailsEvent()
    object Retry : PetitionDetailsEvent()
    object ClearError : PetitionDetailsEvent()
}

sealed class PetitionDetailsResult {
    data class PetitionLoaded(val petition: Petition) : PetitionDetailsResult()
    data class PetitionSigned(val success: Boolean) : PetitionDetailsResult()
    data class PetitionUpdated(val success: Boolean) : PetitionDetailsResult()
    data class PetitionDeleted(val success: Boolean) : PetitionDetailsResult()
    data class Error(val message: String) : PetitionDetailsResult()
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.OfficialPolicyDetailsEvent

sealed class PetitionDetailsEvent {
    data class LoadPetition(val petitionId: String) : PetitionDetailsEvent()
    object SignPetition : PetitionDetailsEvent()


    data class UpdatePetition(val name: String, val imageUrl: String, val otherDetails: Map<String, Any?>) : PetitionDetailsEvent()
    object DeletePetition : PetitionDetailsEvent()
}
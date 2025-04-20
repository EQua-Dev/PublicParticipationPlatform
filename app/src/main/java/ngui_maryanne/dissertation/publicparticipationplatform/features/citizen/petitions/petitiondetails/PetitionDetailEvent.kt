package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

sealed class PetitionDetailsEvent {
    data class LoadPetition(val petitionId: String) : PetitionDetailsEvent()
    object SignPetition : PetitionDetailsEvent()
}
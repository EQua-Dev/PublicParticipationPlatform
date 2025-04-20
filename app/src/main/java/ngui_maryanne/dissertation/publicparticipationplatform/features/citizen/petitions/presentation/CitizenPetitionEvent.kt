package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation


sealed class PetitionEvent {
    data class OnSearchQueryChanged(val query: String) : PetitionEvent()
    object OnToggleCreatePetition : PetitionEvent()
}

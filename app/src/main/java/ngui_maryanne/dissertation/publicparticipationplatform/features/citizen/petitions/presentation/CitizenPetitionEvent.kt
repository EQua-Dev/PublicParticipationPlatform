package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation


sealed class PetitionEvent {
    data class OnSearchQueryChanged(val query: String) : PetitionEvent()
    data class OnSectorFilterChanged(val sector: String?) : PetitionEvent()
    object OnToggleCreatePetition : PetitionEvent()
    object RefreshPetitions : PetitionEvent()
    object OnErrorShown : PetitionEvent()
}
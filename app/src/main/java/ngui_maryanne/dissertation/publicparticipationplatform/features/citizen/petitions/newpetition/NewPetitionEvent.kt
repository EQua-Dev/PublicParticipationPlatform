package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

sealed class NewPetitionEvent {
    data class OnSectorChanged(val value: String) : NewPetitionEvent()
    data class OnTitleChanged(val value: String) : NewPetitionEvent()
    data class OnDescriptionChanged(val value: String) : NewPetitionEvent()
    data class OnCountyChanged(val value: String) : NewPetitionEvent()
    data class OnRequestGoalChanged(val index: Int, val value: String) : NewPetitionEvent()
    object OnAddRequestGoal : NewPetitionEvent()
    data class OnSupportingReasonChanged(val index: Int, val value: String) : NewPetitionEvent()
    object OnAddSupportingReason : NewPetitionEvent()
    data class OnTargetSignatureChanged(val diff: Int) : NewPetitionEvent()
}

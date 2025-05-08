package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.newpetition

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.CreatePolicyEvent

sealed class NewPetitionEvent {
    data class OnSectorChanged(val value: String) : NewPetitionEvent()
    data class OnTitleChanged(val value: String) : NewPetitionEvent()
    data class OnDescriptionChanged(val value: String) : NewPetitionEvent()
    data class OnCountyChanged(val value: String) : NewPetitionEvent()
    data class CoverImageSelected(val uri: Uri) : NewPetitionEvent()
    data class OnRequestGoalChanged(val index: Int, val value: String) : NewPetitionEvent()
    object OnAddRequestGoal : NewPetitionEvent()
    data class OnSupportingReasonChanged(val index: Int, val value: String) : NewPetitionEvent()
    object OnAddSupportingReason : NewPetitionEvent()
    data class OnTargetSignatureChanged(val diff: Int) : NewPetitionEvent()
    data class OnTargetSignatureManuallyChanged(val newValue: Int) : NewPetitionEvent()

}

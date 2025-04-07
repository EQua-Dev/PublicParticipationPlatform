package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

sealed class CreatePolicyEvent {
    data class PolicyNameChanged(val value: String) : CreatePolicyEvent()
    data class PolicyTitleChanged(val value: String) : CreatePolicyEvent()
    data class PolicySectorChanged(val value: String) : CreatePolicyEvent()
    data class PolicyDescriptionChanged(val value: String) : CreatePolicyEvent()
    data class CoverImageSelected(val uri: Uri) : CreatePolicyEvent()
    data class StatusChanged(val status: PolicyStatus) : CreatePolicyEvent()
    object Submit : CreatePolicyEvent()
    object DismissError : CreatePolicyEvent()
    object DismissSuccess : CreatePolicyEvent()
}
package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.policydetails

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.PolicyStatus

sealed class OfficialPolicyDetailsEvent {
    data class LoadData(val policyId: String) : OfficialPolicyDetailsEvent()
    data class UpdateStage(val newStage: PolicyStatus) : OfficialPolicyDetailsEvent()
    object ShowStageDialog : OfficialPolicyDetailsEvent()
    object DismissStageDialog : OfficialPolicyDetailsEvent()
    object DismissError : OfficialPolicyDetailsEvent()
    data class CreatePoll(val policyId: String) : OfficialPolicyDetailsEvent()
}
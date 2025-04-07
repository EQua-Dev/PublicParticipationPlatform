package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll

data class OfficialPolicyDetailsState(
    val policy: Policy? = null,
    val polls: List<Poll> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canCreatePoll: Boolean = false,
    val canUpdateStage: Boolean = false,
    val currentStage: PolicyStatus = PolicyStatus.DRAFT,
    val showStageUpdateDialog: Boolean = false
)
package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.policydetails

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.PolicyStatus
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Comment
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Poll

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
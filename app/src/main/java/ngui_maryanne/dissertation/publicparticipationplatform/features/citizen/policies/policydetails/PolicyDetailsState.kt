package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Comment
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll


data class PolicyDetailsUiState(
    val policy: Policy? = null,
    val polls: List<Poll> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDescriptionExpanded: Boolean = false,
    val isTimelineExpanded: Boolean = false,
    val isPollsExpanded: Boolean = false,
    val isCommentsExpanded: Boolean = false,
    val newCommentText: String = "",
    val isAnonymous: Boolean = false
)
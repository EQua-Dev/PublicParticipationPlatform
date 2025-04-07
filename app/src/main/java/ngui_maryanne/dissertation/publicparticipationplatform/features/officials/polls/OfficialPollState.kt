package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls

import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Poll

data class PollState(
    val polls: List<Poll> = emptyList(),
    val policies: List<Policy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canCreatePoll: Boolean = false
)
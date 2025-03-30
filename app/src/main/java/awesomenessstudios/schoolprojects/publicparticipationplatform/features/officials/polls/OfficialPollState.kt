package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.polls

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Poll

data class PollState(
    val polls: List<Poll> = emptyList(),
    val policies: List<Policy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val canCreatePoll: Boolean = false
)
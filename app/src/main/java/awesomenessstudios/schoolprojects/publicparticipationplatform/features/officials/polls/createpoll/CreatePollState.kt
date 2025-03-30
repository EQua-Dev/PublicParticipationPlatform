package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.polls.createpoll

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.PollOption

data class CreatePollState(
    val pollQuestion: String = "",
    val selectedPolicy: Policy? = null,
    val expiryDays: Int = 7,
    val options: List<PollOption> = listOf(PollOption()),
    val policies: List<Policy> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val createSuccess: Boolean = false
)
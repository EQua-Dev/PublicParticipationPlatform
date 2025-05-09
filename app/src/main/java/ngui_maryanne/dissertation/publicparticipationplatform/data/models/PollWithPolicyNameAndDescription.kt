package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.PollStatus

data class PollWithPolicyNameAndDescription(
    val poll: Poll,
    val policyName: String,
    val policyDescription: String
) {
    val pollStatus: PollStatus get() = if (poll.pollExpiry.toLong() > System.currentTimeMillis()) PollStatus.ACTIVE else PollStatus.CLOSED
}

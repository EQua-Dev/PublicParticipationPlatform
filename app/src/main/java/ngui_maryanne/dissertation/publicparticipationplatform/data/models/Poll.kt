package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Poll(
    val id: String = "",
    val hash: String = "",
    val pollNo: String = "",
    val policyId: String = "",
    val pollQuestion: String = "",
    val pollOptions: List<PollOption> = listOf(),
    val responses: List<PollResponses> = listOf(),
    val createdBy: String = "",
    val dateCreated: String = "",
    val pollExpiry: String = ""
)

data class PollOption(
    val optionId: String = "",
    val optionText: String = "",
    val optionExplanation: String = "",
)

data class PollResponses(
    val answerId: String = "",
    val answerHash: String = "",
    val userId: String = "",
    val optionId: String = "",
    val isAnonymous: String = "",
    val dateCreated: String = ""
)

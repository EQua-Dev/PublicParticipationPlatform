package ngui_maryanne.dissertation.publicparticipationplatform.data.models

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

data class Policy(
    val id: String = "",
    val policyName: String = "",
    val policyHash: String = "",
    val policyStatus: PolicyStatus = PolicyStatus.DRAFT,
    val policyTitle: String = "",
    val policySector: String = "",
    val policyDescription: String = "",
    val policyCoverImage: String = "",
    val statusHistory: List<StatusChange> = emptyList(),
    val dateCreated: String = "",
    val createdBy: String = "",
)

data class StatusChange(
    val status: PolicyStatus = PolicyStatus.DRAFT,
    val changedAt: String = "",
    val changedBy: String = "",
    val notes: String = ""
)
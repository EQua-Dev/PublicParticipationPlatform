package ngui_maryanne.dissertation.publicparticipationplatform.data.enums

enum class PolicyStatus(
    val displayName: String,
    val description: String,
    val isPublicVisible: Boolean
) {
    DRAFT(
        displayName = "Draft",
        description = "Initial drafting stage (internal only)",
        isPublicVisible = false
    ),
    INTERNAL_REVIEW(
        displayName = "Internal Review",
        description = "Under government agency review",
        isPublicVisible = false
    ),
    MINISTERIAL_APPROVAL(
        displayName = "Ministerial Approval",
        description = "Awaiting ministerial sign-off",
        isPublicVisible = false
    ),
    PUBLIC_CONSULTATION(
        displayName = "Public Consultation",
        description = "Open for public feedback",
        isPublicVisible = true
    ),
    PUBLIC_OPINION_ANALYSIS(
        displayName = "Public Opinion Analysis",
        description = "Analyzing citizen feedback",
        isPublicVisible = true
    ),
    REVISED_DRAFT(
        displayName = "Revised Draft",
        description = "Incorporating public feedback",
        isPublicVisible = false
    ),
    CABINET_APPROVAL(
        displayName = "Cabinet Approval",
        description = "Awaiting cabinet decision",
        isPublicVisible = false
    ),
    LEGISLATIVE_PROCESS(
        displayName = "Legislative Process",
        description = "In parliamentary discussion",
        isPublicVisible = true
    ),
    APPROVED(
        displayName = "Approved",
        description = "Policy enacted",
        isPublicVisible = true
    ),
    REJECTED(
        displayName = "Rejected",
        description = "Policy not proceeding",
        isPublicVisible = true
    ),
    ARCHIVED(
        displayName = "Archived",
        description = "Historical policy record",
        isPublicVisible = true
    );

    companion object {
        fun getDefault() = DRAFT
        fun getPublicStages() = entries.filter { it.isPublicVisible }
        fun getInternalStages() = entries.filterNot { it.isPublicVisible }
    }
}
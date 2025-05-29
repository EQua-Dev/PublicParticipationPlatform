package ngui_maryanne.dissertation.publicparticipationplatform.data.enums

import androidx.annotation.StringRes
import ngui_maryanne.dissertation.publicparticipationplatform.R

enum class PolicyStatus(
    @StringRes val displayName: Int,
    @StringRes val description: Int,
    val isPublicVisible: Boolean
) {
    DRAFT(
        displayName = R.string.draft,
        description = R.string.initial_drafting_stage_internal_only,
        isPublicVisible = false
    ),
    INTERNAL_REVIEW(
        displayName = R.string.internal_review,
        description = R.string.under_government_agency_review,
        isPublicVisible = false
    ),
    MINISTERIAL_APPROVAL(
        displayName = R.string.ministerial_approval,
        description = R.string.awaiting_ministerial_sign_off,
        isPublicVisible = false
    ),
    PUBLIC_CONSULTATION(
        displayName = R.string.public_consultation,
        description = R.string.open_for_public_feedback,
        isPublicVisible = true
    ),
    PUBLIC_OPINION_ANALYSIS(
        displayName = R.string.public_opinion_analysis,
        description = R.string.analyzing_citizen_feedback,
        isPublicVisible = true
    ),
    REVISED_DRAFT(
        displayName = R.string.revised_draft,
        description = R.string.incorporating_public_feedback,
        isPublicVisible = false
    ),
    CABINET_APPROVAL(
        displayName = R.string.cabinet_approval,
        description = R.string.awaiting_cabinet_decision,
        isPublicVisible = false
    ),
    LEGISLATIVE_PROCESS(
        displayName = R.string.legislative_process,
        description = R.string.in_parliamentary_discussion,
        isPublicVisible = true
    ),
    APPROVED(
        displayName = R.string.approved,
        description = R.string.policy_enacted,
        isPublicVisible = true
    ),
    REJECTED(
        displayName = R.string.rejected,
        description = R.string.policy_not_proceeding,
        isPublicVisible = true
    ),
    ARCHIVED(
        displayName = R.string.archived,
        description = R.string.historical_policy_record,
        isPublicVisible = true
    );

    companion object {
        fun getDefault() = DRAFT
        fun getPublicStages() = entries.filter { it.isPublicVisible }
        fun getInternalStages() = entries.filterNot { it.isPublicVisible }
    }
}
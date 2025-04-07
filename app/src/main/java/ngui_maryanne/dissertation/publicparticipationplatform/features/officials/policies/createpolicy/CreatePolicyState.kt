package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus

data class CreatePolicyState(
    val policyName: String = "",
    val policyTitle: String = "",
    val policySector: String = "",
    val policyDescription: String = "",
    val coverImageUri: Uri? = null,
    val availableStatuses: List<PolicyStatus> = PolicyStatus.entries,
    val selectedStatus: PolicyStatus = PolicyStatus.DRAFT,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val sectors: List<String> = listOf(
        "Education", "Health", "Agriculture",
        "Infrastructure", "Finance", "Technology"
    )
)
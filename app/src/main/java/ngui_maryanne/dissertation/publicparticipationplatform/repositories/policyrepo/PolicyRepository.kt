package ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import com.google.firebase.firestore.ListenerRegistration

interface PolicyRepository {
    suspend fun getAllPolicies(): List<Policy>
    suspend fun createPolicy(policy: Policy)
    suspend fun getPoliciesBeforePublicOpinion(): List<Policy>
    fun getPolicyListener(policyId: String, onUpdate: (Policy?) -> Unit): ListenerRegistration
    suspend fun updatePolicyStage(policyId: String, newStage: PolicyStatus)

}
package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.policyrepo

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.PolicyStatus
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy
import com.google.firebase.firestore.ListenerRegistration

interface PolicyRepository {
    suspend fun getAllPolicies(): List<Policy>
    suspend fun createPolicy(policy: Policy)
    suspend fun getPoliciesBeforePublicOpinion(): List<Policy>
    fun getPolicyListener(policyId: String, onUpdate: (Policy?) -> Unit): ListenerRegistration
    suspend fun updatePolicyStage(policyId: String, newStage: PolicyStatus)

}
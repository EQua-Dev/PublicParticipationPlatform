package ngui_maryanne.dissertation.publicparticipationplatform.repositories.policyrepo

import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.PolicyStatus
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Policy
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow

interface PolicyRepository {
    fun getAllPolicies(): Flow<List<Policy>>
    suspend fun createPolicy(policy: Policy)
    suspend fun getPoliciesBeforePublicOpinion(): List<Policy>
    fun getPolicyListener(policyId: String, onUpdate: (Policy?) -> Unit): ListenerRegistration
    suspend fun updatePolicyStage(policyId: String, newStage: PolicyStatus)
    suspend fun getPublicPolicies(): Flow<List<Policy>>
    suspend fun searchPolicies(query: String): Flow<List<Policy>>
    suspend fun getPolicy(policyId: String): Flow<Policy?>
    suspend fun updatePolicy(policyId: String, name: String, imageUrl: String, otherDetails: Map<String, Any?>)
    suspend fun deletePolicy(policyId: String)


}
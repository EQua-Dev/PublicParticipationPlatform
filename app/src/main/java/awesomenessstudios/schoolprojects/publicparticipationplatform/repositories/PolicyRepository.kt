package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Policy

interface PolicyRepository {
    suspend fun getAllPolicies(): List<Policy>
    suspend fun createPolicy(policy: Policy)


}
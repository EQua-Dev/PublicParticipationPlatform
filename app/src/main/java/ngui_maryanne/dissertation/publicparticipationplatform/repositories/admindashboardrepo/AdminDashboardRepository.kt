package ngui_maryanne.dissertation.publicparticipationplatform.repositories.admindashboardrepo

import kotlinx.coroutines.flow.Flow

interface AdminDashboardRepository {

    suspend fun getCitizenCount(): Flow<Int>
    suspend fun getOfficialCount(): Flow<Int>
    suspend fun getPoliciesCount(): Flow<Int>
    suspend fun getPollsCount(): Flow<Int>
    suspend fun getBudgetsCount(): Flow<Int>
    suspend fun getPetitionsCount(): Flow<Int>

}
package ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

interface OfficialsRepository {
    suspend fun createOfficial(
        official: Official,
        profileImageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    )
    fun getOfficialsRealtime(onResult: (List<Official>, String?) -> Unit)

    suspend fun getCurrentOfficial(): Official
    suspend fun updateOfficial(official: Official)
    fun getOfficialByIdRealtime(
        officialId: String
    ): Flow<Official?>



}
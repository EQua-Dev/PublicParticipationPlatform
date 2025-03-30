package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official

interface OfficialsRepository {
    suspend fun createOfficial(
        official: Official,
        profileImageUri: Uri?,
        onResult: (Boolean, String?) -> Unit
    )
    fun getOfficialsRealtime(onResult: (List<Official>, String?) -> Unit)

    suspend fun getCurrentOfficial(): Official
    suspend fun updateOfficial(official: Official)



}
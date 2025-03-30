package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.net.Uri
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.NationalCitizen

interface NationalDBRepository {
    fun addCitizen(citizen: NationalCitizen, imageUri: Uri?, onResult: (Boolean, String?) -> Unit)

    suspend fun getCitizenByNationalId(nationalId: String): NationalCitizen?

}
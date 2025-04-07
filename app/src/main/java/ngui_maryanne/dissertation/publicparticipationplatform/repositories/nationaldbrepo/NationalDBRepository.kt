package ngui_maryanne.dissertation.publicparticipationplatform.repositories.nationaldbrepo

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.NationalCitizen

interface NationalDBRepository {
    fun addCitizen(citizen: NationalCitizen, imageUri: Uri?, onResult: (Boolean, String?) -> Unit)

    suspend fun getCitizenByNationalId(nationalId: String): NationalCitizen?

}
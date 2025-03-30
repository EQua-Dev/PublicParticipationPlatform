package awesomenessstudios.schoolprojects.publicparticipationplatform.repositories

import android.app.Activity
import android.net.Uri
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.NationalCitizen

interface CitizenRepository {
    suspend fun getAllCitizens(): List<Citizen>
    suspend fun sendOtp(phoneNumber: String, activity: Activity): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit>
    suspend fun registerCitizen(citizen: Citizen,  imageUri: Uri?): Result<Unit>
    suspend fun updateCitizenDetails(citizenId: String, details: Map<String, Any>): Result<Unit>
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String>

    suspend fun approveCitizen(citizenId: String)
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Country

// CitizenRegistrationState.kt
data class CitizenRegistrationState(
    // Step 1 Fields
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val nationalID: String = "",
    val phoneNumber: String = "",
    val country: Country = Country("Nigeria", "+234", "NG", "\uD83C\uDDF3\uD83C\uDDEC"),
    val registrationLocation: String = "",
    val otp: String = "",

    // Step 2 Fields
    val profileImage: Uri? = null,
    val occupation: String = "",
    val countyOfResidence: String = "",
    val countyOfBirth: String = "",

    // UI State
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false,
    val isOtpVerified: Boolean = false,
    val isRegistrationComplete: Boolean = false,
    val errorMessage: String? = null,
    val currentStep: Int = 1
)
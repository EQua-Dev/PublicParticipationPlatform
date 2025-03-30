package awesomenessstudios.schoolprojects.publicparticipationplatform.features.common.auth.presentation.signup

import android.app.Activity
import android.net.Uri

// CitizenRegistrationEvent.kt
sealed class CitizenRegistrationEvent {
    // Step 1 Events
    data class FirstNameChanged(val firstName: String) : CitizenRegistrationEvent()
    data class LastNameChanged(val lastName: String) : CitizenRegistrationEvent()
    data class EmailChanged(val email: String) : CitizenRegistrationEvent()
    data class NationalIDChanged(val nationalID: String) : CitizenRegistrationEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : CitizenRegistrationEvent()
    data class RegistrationLocationChanged(val location: String) : CitizenRegistrationEvent()

    // OTP Events
    data class SendOtp(val activity: Activity) : CitizenRegistrationEvent()
//    data class OtpSent(val otp: String) : CitizenRegistrationEvent()
    data class OtpChanged(val otp: String) : CitizenRegistrationEvent()
    object VerifyOtp : CitizenRegistrationEvent()

    // Step 2 Events
    data class ProfileImageChanged(val imageUrl: Uri) : CitizenRegistrationEvent()
    data class OccupationChanged(val occupation: String) : CitizenRegistrationEvent()
    data class CountyOfResidenceChanged(val county: String) : CitizenRegistrationEvent()
    data class CountyOfBirthChanged(val county: String) : CitizenRegistrationEvent()

    // Registration Events
    object CompleteRegistration : CitizenRegistrationEvent()
    object ResetRegistration : CitizenRegistrationEvent()
}
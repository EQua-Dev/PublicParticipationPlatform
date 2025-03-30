package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.otpverification

sealed class OtpVerificationEvent {
    data class OtpChanged(val otp: String) : OtpVerificationEvent()
    object VerifyOtp : OtpVerificationEvent()
}
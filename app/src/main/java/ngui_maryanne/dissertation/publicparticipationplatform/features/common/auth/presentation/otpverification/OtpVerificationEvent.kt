package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.otpverification

sealed class OtpVerificationEvent {
    data class OtpChanged(val otp: String) : OtpVerificationEvent()
    object VerifyOtp : OtpVerificationEvent()
}
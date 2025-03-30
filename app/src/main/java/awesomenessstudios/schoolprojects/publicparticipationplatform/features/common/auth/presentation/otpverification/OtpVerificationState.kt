package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.otpverification

data class OtpVerificationState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val isVerificationSuccessful: Boolean = false,
    val errorMessage: String? = null
)

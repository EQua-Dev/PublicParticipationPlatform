package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.forgotpassword

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isPasswordResetSuccessful: Boolean = false,
    val errorMessage: String? = null
)
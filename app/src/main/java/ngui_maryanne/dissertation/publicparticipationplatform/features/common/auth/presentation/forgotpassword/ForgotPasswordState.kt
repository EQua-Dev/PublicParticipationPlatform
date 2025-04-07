package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.forgotpassword

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isPasswordResetSuccessful: Boolean = false,
    val errorMessage: String? = null
)
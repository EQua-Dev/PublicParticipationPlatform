package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.forgotpassword

sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    object ResetPassword : ForgotPasswordEvent()
}
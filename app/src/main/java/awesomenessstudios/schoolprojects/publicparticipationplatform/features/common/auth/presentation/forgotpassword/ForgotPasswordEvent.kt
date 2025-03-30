package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.forgotpassword

sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    object ResetPassword : ForgotPasswordEvent()
}
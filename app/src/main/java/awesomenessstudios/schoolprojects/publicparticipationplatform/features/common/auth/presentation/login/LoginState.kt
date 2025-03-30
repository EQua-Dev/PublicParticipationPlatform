package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null,
    val userRole: String? = null

)
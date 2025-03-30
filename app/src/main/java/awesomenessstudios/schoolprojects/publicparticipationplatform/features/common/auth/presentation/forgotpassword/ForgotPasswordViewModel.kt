package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.forgotpassword

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Common.mAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(): ViewModel() {
    private val _state = mutableStateOf(ForgotPasswordState())
    val state: State<ForgotPasswordState> = _state

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            ForgotPasswordEvent.ResetPassword -> {
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val email = _state.value.email

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isPasswordResetSuccessful = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Password reset failed"
                    )
                }
            }
    }
}
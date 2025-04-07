package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.otpverification

import com.google.firebase.auth.PhoneAuthProvider
import androidx.lifecycle.ViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Common.mAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.otpverification.OtpVerificationEvent
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.otpverification.OtpVerificationState
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OtpVerificationState())
    val state: StateFlow<OtpVerificationState> = _state

    private var verificationId: String? = null // Store the verification ID from Firebase

    fun onEvent(event: OtpVerificationEvent) {
        when (event) {
            is OtpVerificationEvent.OtpChanged -> {
                _state.value = _state.value.copy(otp = event.otp)
            }
            OtpVerificationEvent.VerifyOtp -> {
                verifyOtp()
            }
        }
    }

    private fun verifyOtp() {
        val otp = _state.value.otp
        if (otp.length != 6) {
            _state.value = _state.value.copy(errorMessage = "Invalid OTP")
            return
        }

        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isVerificationSuccessful = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "OTP verification failed"
                    )
                }
            }
    }

    // Set the verification ID (to be called from the previous screen)
    fun setVerificationId(verificationId: String) {
        this.verificationId = verificationId
    }
}
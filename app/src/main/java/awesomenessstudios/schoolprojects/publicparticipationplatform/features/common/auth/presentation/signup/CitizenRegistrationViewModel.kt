package awesomenessstudios.schoolprojects.publicparticipationplatform.features.common.auth.presentation.signup

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.CitizenRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Common.mAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// CitizenRegistrationViewModel.kt
@HiltViewModel
class CitizenRegistrationViewModel @Inject constructor(
    private val repository: CitizenRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = mutableStateOf(CitizenRegistrationState())
    val state: State<CitizenRegistrationState> = _state

    private var verificationId: String? = null // Store the verification ID from Firebase
    private var citizenId: String? = null

    fun onEvent(event: CitizenRegistrationEvent) {
        when (event) {
            // Step 1 Field Updates
            is CitizenRegistrationEvent.FirstNameChanged -> {
                _state.value = _state.value.copy(firstName = event.firstName)
            }

            is CitizenRegistrationEvent.LastNameChanged -> {
                _state.value = _state.value.copy(lastName = event.lastName)
            }

            is CitizenRegistrationEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }

            is CitizenRegistrationEvent.NationalIDChanged -> {
                _state.value = _state.value.copy(nationalID = event.nationalID)
            }

            is CitizenRegistrationEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }

            is CitizenRegistrationEvent.RegistrationLocationChanged -> {
                _state.value = _state.value.copy(registrationLocation = event.location)
            }

            // OTP Handling
            is CitizenRegistrationEvent.SendOtp -> {
                sendOtp(event.activity)
            }

            is CitizenRegistrationEvent.OtpChanged -> {
                _state.value = _state.value.copy(otp = event.otp)
            }

            CitizenRegistrationEvent.VerifyOtp -> verifyOtp()

            // Step 2 Field Updates
            is CitizenRegistrationEvent.ProfileImageChanged -> {
                _state.value = _state.value.copy(profileImage = event.imageUrl)
            }

            is CitizenRegistrationEvent.OccupationChanged -> {
                _state.value = _state.value.copy(occupation = event.occupation)
            }

            is CitizenRegistrationEvent.CountyOfResidenceChanged -> {
                _state.value = _state.value.copy(countyOfResidence = event.county)
            }

            is CitizenRegistrationEvent.CountyOfBirthChanged -> {
                _state.value = _state.value.copy(countyOfBirth = event.county)
            }

            // Registration Completion
            CitizenRegistrationEvent.CompleteRegistration -> completeRegistration()
            CitizenRegistrationEvent.ResetRegistration -> {
                _state.value = CitizenRegistrationState()
            }
        }
    }

    /*private fun sendOtp(activity: Activity) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.sendOtp(_state.value.phoneNumber, activity)
            _state.value = _state.value.copy(
                isLoading = false,
                isOtpSent = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }
*/
    /*private fun verifyOtp() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val result = repository.verifyOtp(
                _state.value.phoneNumber,
                _state.value.otp
            )
            _state.value = _state.value.copy(
                isLoading = false,
                isOtpVerified = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message,
                currentStep = if (result.isSuccess) 2 else 1
            )
        }
    }*/


    private fun sendOtp(activity: Activity) {
        Log.d("TAG", "sendOtp: ${_state.value.phoneNumber}")
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(_state.value.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity) // Pass the current activity
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (e.g., SMS retriever)
//                    signInWithPhoneAuthCredential(credential)
                    val otp = credential.smsCode // Get the OTP from the credential
                    Log.d("TAG", "onVerificationCompleted: ${credential.smsCode}")
                    if (otp != null) {
//                        onEvent(CitizenRegistrationEvent.OtpSent(otp)) // Store the sent OTP
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to send OTP"
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    Log.d("TAG", "onCodeSent: $verificationId")
                    this@CitizenRegistrationViewModel.verificationId = verificationId
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isOtpSent = true
                    )
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp() {
        val enteredOtp = _state.value.otp
//        val sentOtp = _state.value.sentOtp
        if (enteredOtp.length != 6) {
            _state.value = _state.value.copy(errorMessage = "Invalid OTP")
            return
        }


        _state.value = _state.value.copy(isLoading = true, errorMessage = null)


        /*  if (enteredOtp == sentOtp) {
              // OTP verification successful
              updateVerificationStatus()
          } else {
              // OTP verification failed
              _state.value = _state.value.copy(
                  isLoading = false,
                  errorMessage = "Incorrect OTP"
              )
          }*/
        val credential = PhoneAuthProvider.getCredential(verificationId!!, enteredOtp)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    updateVerificationStatus()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isOtpVerified = task.isSuccessful,
//                        errorMessage = result.exceptionOrNull()?.message,
                        currentStep = 2
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "OTP verification failed",
                        currentStep = 2
                    )
                }
            }
    }

    /*private fun updateVerificationStatus() {
        val userId = _state.value.newUserId
        viewModelScope.launch {
            userPreferences.saveUserId(userId)
        }

        teachersCollectionRef.document(userId)
            .update("isVerified", true)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isVerificationSuccessful = true,
                    isRegistrationSuccessful = true
                )
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update verification status"
                )
            }
    }*/

    private fun completeRegistration() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // First create basic citizen record
            val citizen = Citizen(
                id = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                email = _state.value.email,
                nationalID = _state.value.nationalID,
                phoneNumber = _state.value.phoneNumber,
                registrationLocation = _state.value.registrationLocation,
//                profileImage = _state.value.profileImage,
                occupation = _state.value.occupation,
                countyOfResidence = _state.value.countyOfResidence,
                countyOfBirth = _state.value.countyOfBirth,
                dateCreated = System.currentTimeMillis().toString()
            )

            // Register citizen
            val registerResult = repository.registerCitizen(citizen, _state.value.profileImage)

            _state.value = _state.value.copy(
                isLoading = false,
                isRegistrationComplete = registerResult.isSuccess,
                errorMessage = registerResult.exceptionOrNull()?.message
            )
        }
    }
}

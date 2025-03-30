package awesomenessstudios.schoolprojects.publicparticipationplatform.features.common.auth.presentation.signup

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CitizenRegistrationStepOne(
    state: CitizenRegistrationState,
    onEvent: (CitizenRegistrationEvent) -> Unit,
    onOtpVerified: () -> Unit
) {


    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Citizen Registration - Step 1",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.firstName,
            onValueChange = { onEvent(CitizenRegistrationEvent.FirstNameChanged(it)) },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.lastName,
            onValueChange = { onEvent(CitizenRegistrationEvent.LastNameChanged(it)) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { onEvent(CitizenRegistrationEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = state.nationalID,
            onValueChange = { onEvent(CitizenRegistrationEvent.NationalIDChanged(it)) },
            label = { Text("National ID") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = { onEvent(CitizenRegistrationEvent.PhoneNumberChanged(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        OutlinedTextField(
            value = state.registrationLocation,
            onValueChange = { onEvent(CitizenRegistrationEvent.RegistrationLocationChanged(it)) },
            label = { Text("Registration Location") },
            modifier = Modifier.fillMaxWidth()
        )

        if (!state.isOtpSent) {
            Button(
                onClick = { onEvent(CitizenRegistrationEvent.SendOtp(activity!!)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.firstName.isNotBlank() &&
                        state.lastName.isNotBlank() &&
                        state.phoneNumber.isNotBlank()
            ) {
                Text("Send OTP")
            }
        } else {
            OutlinedTextField(
                value = state.otp,
                onValueChange = { onEvent(CitizenRegistrationEvent.OtpChanged(it)) },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = { onEvent(CitizenRegistrationEvent.VerifyOtp) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.otp.length == 6
            ) {
                Text("Verify OTP")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Navigate to step 2 when OTP is verified
        LaunchedEffect(state.isOtpVerified) {
            if (state.isOtpVerified) {
                onOtpVerified()
            }
        }
    }
}
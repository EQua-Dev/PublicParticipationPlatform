package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.PasswordTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.PhoneNumberInput
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.LoginEvent
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.PublicParticipationPlatformTheme

@Composable
fun CitizenRegistrationStepOne(
    state: CitizenRegistrationState,
    onEvent: (CitizenRegistrationEvent) -> Unit,
    onOtpVerified: () -> Unit
) {


    val context = LocalContext.current
    val activity = context as? Activity

    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (!state.isOtpSent) {

            Text(
                text = "Citizen Registration - Step 1",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )

            CustomTextField(
                value = state.firstName,
                onValueChange = { onEvent(CitizenRegistrationEvent.FirstNameChanged(it)) },
                label = stringResource(id = R.string.first_name_label),
                keyboardType = KeyboardType.Text
            )

            CustomTextField(
                value = state.lastName,
                onValueChange = { onEvent(CitizenRegistrationEvent.LastNameChanged(it)) },
                label = stringResource(id = R.string.last_name_label),
                keyboardType = KeyboardType.Text
            )
            CustomTextField(
                value = state.email,
                onValueChange = { onEvent(CitizenRegistrationEvent.EmailChanged(it)) },
                label = stringResource(id = R.string.email_label),
                keyboardType = KeyboardType.Email
            )

            PasswordTextField(
                password = state.password,
                onPasswordChange = { onEvent(CitizenRegistrationEvent.PasswordChanged(it)) })

            CustomTextField(
                value = state.nationalID,
                onValueChange = { onEvent(CitizenRegistrationEvent.NationalIDChanged(it)) },
                label = stringResource(id = R.string.national_id_label),
                keyboardType = KeyboardType.Text
            )

            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { onEvent(CitizenRegistrationEvent.PhoneNumberChanged(it)) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

//        PhoneNumberInput(
//            selectedCountry = state.country,
//            onCountryChange = { onEvent(CitizenRegistrationEvent.CountryChanged(it)) },
//            phoneNumber = state.phoneNumber,
//            onPhoneNumberChange = { onEvent(CitizenRegistrationEvent.PhoneNumberChanged(it.trim())) }
//        )

            OutlinedTextField(
                value = state.registrationLocation,
                onValueChange = { onEvent(CitizenRegistrationEvent.RegistrationLocationChanged(it)) },
                label = { Text("Registration Location") },
                modifier = Modifier.fillMaxWidth()
            )

            CustomButton(
                text = stringResource(id = R.string.send_otp),
                onClick = { onEvent(CitizenRegistrationEvent.SendOtp(activity!!)) },
                enabled = state.firstName.isNotBlank() &&
                        state.lastName.isNotBlank() &&
                        state.phoneNumber.isNotBlank() &&
                !state.isLoading
            )
            /*  Button(
                  onClick = { onEvent(CitizenRegistrationEvent.SendOtp(activity!!)) },
                  modifier = Modifier.fillMaxWidth(),
                  enabled = state.firstName.isNotBlank() &&
                          state.lastName.isNotBlank() &&
                          state.phoneNumber.isNotBlank()
              ) {
                  Text("Send OTP")
              }*/
        } else if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Column {
                    Text(text = "Enter OTP")
                    CustomTextField(
                        value = state.otp,
                        onValueChange = { onEvent(CitizenRegistrationEvent.OtpChanged(it)) },
                        label ="OTP",
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Number,
                    )

                    Button(
                        onClick = { onEvent(CitizenRegistrationEvent.VerifyOtp) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.otp.length == 6
                    ) {
                        Text("Verify OTP")
                    }
                }
            }
         
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(
                    Alignment
                        .CenterHorizontally
                )
                .padding(16.dp)
        )

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
            }
        }


        // Navigate to step 2 when OTP is verified
        LaunchedEffect(state.isOtpVerified) {
            if (state.isOtpVerified) {
                onOtpVerified()
            }
        }
    }
}

@Preview (showBackground = true, showSystemUi = true)
@Composable
private fun OTPScreenPreview() {
    PublicParticipationPlatformTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp), contentAlignment = Alignment.Center){
            Column {
                Text(text = "Enter OTP", textAlign = TextAlign.Center)
                CustomTextField(
                    value = "",
                    onValueChange = {  },
                    label ="OTP",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Number,
                )

                Spacer(modifier = Modifier.height(24.dp))
                CustomButton(
                    text = "Verify OTP",
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth(),

                )
            }
        }
    }

}
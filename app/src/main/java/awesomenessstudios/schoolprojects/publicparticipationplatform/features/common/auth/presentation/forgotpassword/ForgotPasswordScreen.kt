package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onPasswordResetSuccess: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email Field
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(ForgotPasswordEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Password Button
        Button(
            onClick = { viewModel.onEvent(ForgotPasswordEvent.ResetPassword) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password")
        }

        // Loading and Error Handling
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let { error ->
            Text(text = error, color = Color.Red)
        }

        // Navigate on Success
        LaunchedEffect(state.isPasswordResetSuccessful) {
            if (state.isPasswordResetSuccessful) {
                onPasswordResetSuccess()
            }
        }
    }
}
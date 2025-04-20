package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomTextField
import ngui_maryanne.dissertation.publicparticipationplatform.components.PasswordTextField
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.ButtonIconPosition
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LoadingDialog

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (role: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onRegisterClicked: (role: String) -> Unit
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }


    if (state.isLoading) {
        LoadingDialog(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) // 50% opacity)
//            CircularProgressIndicator()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = stringResource(id = R.string.login_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(id = R.string.login_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = null,
            modifier = Modifier.weight(0.2f),
            contentScale = ContentScale.Fit // or .FillBounds / .Fit depending on effect
        )

        Spacer(modifier = Modifier.height(32.dp))
        // Email Field
        CustomTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
            label = "Email",
            keyboardType = KeyboardType.Email
        )
        /* OutlinedTextField(
             value = state.email,
             onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
             label = { Text("Email") },
             modifier = Modifier.fillMaxWidth(),

         )*/

        Spacer(modifier = Modifier.height(8.dp))

        // Password Field
        PasswordTextField(
            password = state.password,
            onPasswordChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) })
        /*  OutlinedTextField(
              value = state.password,
              onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
              label = { Text("Password") },
              modifier = Modifier.fillMaxWidth(),
              visualTransformation = PasswordVisualTransformation()
          )*/

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        CustomButton(
            text = "Login",
            onClick = { viewModel.onEvent(LoginEvent.Login) },
            iconPosition = ButtonIconPosition.START,
            icon = Icons.Default.Login,
        )
        /*   Button(
               onClick = { viewModel.onEvent(LoginEvent.Login) },
               modifier = Modifier.fillMaxWidth()
           ) {
               Text("Login")
           }*/

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password Link
        if (state.userRole != UserRole.SUPERADMIN.name)
            TextButton(onClick = onForgotPasswordClicked) {
                Text("Forgot Password?")
            }

        Spacer(modifier = Modifier.height(24.dp))

        // Create Account Link
        if (state.userRole == UserRole.CITIZEN.name)
            TextButton(onClick = { onRegisterClicked(state.userRole!!) }) {
                Text("Create Account")
            }

        // Loading and Error Handling


        /*state.errorMessage?.let { error ->
            Text(text = error, color = Color.Red)
        }*/

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

        // Navigate on Success
        LaunchedEffect(state.isLoginSuccessful) {
            if (state.isLoginSuccessful) {
                onLoginSuccess(state.userRole!!)
            }
        }
    }
}
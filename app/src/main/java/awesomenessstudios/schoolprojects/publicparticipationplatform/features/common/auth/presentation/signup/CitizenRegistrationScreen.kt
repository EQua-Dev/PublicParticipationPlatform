package awesomenessstudios.schoolprojects.publicparticipationplatform.features.common.auth.presentation.signup

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.publicparticipationplatform.R
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.Constants.subjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenRegistrationScreen(
    viewModel: CitizenRegistrationViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit
) {
    val state = viewModel.state.value

    if (state.currentStep == 1) {
        CitizenRegistrationStepOne(
            state = state,
            onEvent = viewModel::onEvent,
            onOtpVerified = { /* State is updated automatically */ }
        )
    } else {
        CitizenRegistrationStepTwo(
            state = state,
            onEvent = viewModel::onEvent,
            onRegistrationComplete = onRegistrationSuccess
        )
    }
}
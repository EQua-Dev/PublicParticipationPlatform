package ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

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
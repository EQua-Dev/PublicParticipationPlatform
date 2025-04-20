package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.PollWithPolicyNameAndDescription
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PollDetailsScreen(
    pollId: String,
    navController: NavHostController,
    viewModel: PollDetailsViewModel = hiltViewModel(), // or pass it in

) {
    val state by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onEvent(PollDetailsEvent.LoadPollDetails(pollId))
    }

    when {
        state.isLoading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error ?: "Error loading data")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.onEvent(PollDetailsEvent.Retry) }) {
                        Text("Retry")
                    }
                }
            }
        }
        state.poll != null -> {
            state.policy?.let {
                PollWithPolicyNameAndDescription(
                    poll = state.poll!!,
                    policyName = it.policyName,
                    policyDescription = state.policy!!.policyDescription
                )
            }?.let {
                PollDetailsContent(
                    pollData = it,
                    onOptionSelected = {  },
                    onViewPolicyClick = { navController.navigate(Screen.CitizenPolicyDetailsScreen.route.replace(
                        "{policyId}",
                        state.policy!!.id
                    )) }
                )
            }
        }
    }
}

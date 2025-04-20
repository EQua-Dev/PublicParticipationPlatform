package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.material.progressindicator.LinearProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.daysToExpiry
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.signaturesProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetitionDetailsScreen(
    petitionId: String,
    viewModel: PetitionDetailsViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val state = viewModel.state.value
    val petition = state.petition

    LaunchedEffect(key1 = petitionId) {
        viewModel.onEvent(PetitionDetailsEvent.LoadPetition(petitionId))
    }

    petition?.let {
        var progress by remember { mutableStateOf(it.signaturesProgress()) }
        val animatedProgress = animateFloatAsState(
            targetValue = progress,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TopAppBar(
                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { navHostController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Text(text = it.title, style = MaterialTheme.typography.titleMedium)
                        }
                        Text("${it.signatures.size}/${it.signatureGoal} signatures")
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text(text = "County: ${it.county}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.description, modifier = Modifier.weight(1f))
                if (state.currentUserId != it.createdBy && !state.hasSigned) {
                    Button(onClick = { viewModel.onEvent(PetitionDetailsEvent.SignPetition) }) {
                        Text("Sign")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Request Goals", style = MaterialTheme.typography.titleSmall)
            it.requestGoals.forEach { goal ->
                Text("- $goal")
            }

            /*  Spacer(Modifier.height(16.dp))
              Text("Supporting Reasons", style = MaterialTheme.typography.titleSmall)
              it.supportingReasons.forEach { reason ->
                  Text("- $reason")
              }
  */
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Target")
                Text("${it.daysToExpiry()} days left")
            }

            Spacer(Modifier.height(8.dp))


            /*       LinearProgressIndicator(
                       progress = { 0.5f },
                       trackColor = Color.Gray,
                       modifier = Modifier
                           .width(200.dp)
                           .height(15.dp),
                       color = Color.Blue,
                       strokeCap = StrokeCap.Round,
                   )
       */
        }
    }
}

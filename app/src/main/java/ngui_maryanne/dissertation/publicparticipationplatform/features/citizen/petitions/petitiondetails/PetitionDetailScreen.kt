package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.material.progressindicator.LinearProgressIndicator
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.daysToExpiry
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.signaturesProgress
import ngui_maryanne.dissertation.publicparticipationplatform.utils.findActivity

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetitionDetailsScreen(
    petitionId: String,
    viewModel: PetitionDetailsViewModel = hiltViewModel(),
    navHostController: NavHostController
) {
    val state = viewModel.state.value
    val petition = state.petition

    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(onClick = { navHostController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Text(text = it.title, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(
                            "${it.signatures.size}/${it.signatureGoal} signatures",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(colorScheme.surfaceVariant)
                        .clip(MaterialTheme.shapes.large)

                ) {

                    if (it.coverImage.isNotEmpty()) {
                        AsyncImage(
                            model = it.coverImage,
                            contentDescription = "Petition cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_petitions),
                            contentDescription = "Petition placeholder",
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center),
                            tint = colorScheme.onSurfaceVariant
                        )
                    }

                }

            Text(text = "County: ${it.county}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.description, modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium)
                if (state.currentUserId != it.createdBy && !state.hasSigned && state.currentUserRole == UserRole.CITIZEN.name) {
                    Button(onClick = { activity?.let { fragmentActivity ->
                        Log.d("TAG", "PetitionDetailsScreen: yes fragment")
                        viewModel.verifyAndSignPetition(
                            activity = fragmentActivity,
                            userId = state.currentUserId,
                            petition = state.petition,
                            hashType = "SHA-256",
                            isAnonymous = false,
                            onSuccess = {
                                viewModel.onEvent(
                                    PetitionDetailsEvent.LoadPetition(
                                        petitionId
                                    )
                                )
                            },
                            onFailure = { error -> viewModel.updateError(error)}
                        )
                    } ?: run {
                        // Handle case where activity isn't available
                        // Maybe show error or use alternative authentication
                        Log.d("TAG", "PetitionDetailsScreen: no fragment")
                    }}) {
                        Text("Sign")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Request Goals", style = MaterialTheme.typography.titleSmall)
            it.requestGoals.forEach { goal ->
                Text("- $goal",
                    style = MaterialTheme.typography.bodyMedium)
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
                Text("${it.daysToExpiry()} days left",
                    style = MaterialTheme.typography.bodyMedium)
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

package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails.EditPetitionBottomSheet
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails.PetitionDetailsEvent
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.utils.findActivity

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailsScreen(
    viewModel: OfficialBudgetViewModel = hiltViewModel(),
    detailsViewModel: OfficialBudgetDetailsViewModel = hiltViewModel(),
    navController: NavHostController,
//    userRole: UserRole, // Either CITIZEN or OFFICIAL
    budgetId: String,
) {
    val state = viewModel.uiState.value
    val detailsState = detailsViewModel.uiState.value

    val showBottomSheet = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    LaunchedEffect(Unit) {
        detailsViewModel.onEvent(BudgetDetailsEvent.LoadBudget(budgetId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Total Budget: KSH${detailsState.budget?.amount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Impact: ${detailsState.budget?.impact}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Note: ${detailsState.budget?.budgetNote}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Budget Options", style = MaterialTheme.typography.titleMedium)

            detailsState.budgetOptions.forEachIndexed { index, option ->
                Spacer(modifier = Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            "Project: ${option.optionProjectName}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "Description: ${option.optionDescription}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Amount: KSH${option.optionAmount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        option.imageUrl.let {
                            AsyncImage(
                                model = it,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(140.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (state.currentUserRole.lowercase() == UserRole.CITIZEN.name.lowercase()) {
                            val votedOptionId = detailsState.votedOptionId
                            if (votedOptionId == null) {
                                CustomButton(
                                    text = "Vote This Option",
                                    onClick = {
                                        activity?.let { fragmentActivity ->
                                            detailsViewModel.verifyAndVoteOption(
                                                activity = fragmentActivity,
                                                optionId = option.optionId,
                                                optionName = option.optionProjectName,
                                                hashType = "SHA-256",
                                                isAnonymous = false,
                                                onSuccess = {
                                                    detailsViewModel.onEvent(
                                                        BudgetDetailsEvent.LoadBudget(budgetId)
                                                    )
                                                },
                                                onFailure = { }
                                            )
                                        } ?: run {
                                            // Handle case where activity isn't available
                                            // Maybe show error or use alternative authentication
                                            Log.d("TAG", "PetitionDetailsScreen: no fragment")
                                        }
//                                        detailsViewModel.onEvent(BudgetDetailsEvent.VoteOption(option.optionId))
                                    }
                                )
                            } else if (votedOptionId == option.optionId) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified, // Choose the icon you want
                                        contentDescription = "Voted",
                                        tint = Color.Green
                                    )
                                    Spacer(modifier = Modifier.width(4.dp)) // Adds some space between the icon and text
                                    Text(
                                        text = "You voted for this option",
                                        color = Color.Green,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.currentUserRole.lowercase() == UserRole.OFFICIAL.name.lowercase()) {
                CustomButton(
                    text = if (detailsState.budget?.isActive == true) "Deactivate Budget" else "Activate Budget",
                    onClick = { detailsViewModel.onEvent(BudgetDetailsEvent.ToggleActivation) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "Edit Budget Details",
                    onClick = {
                        showBottomSheet.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Show bottom sheet if the user is the creator of the petition
        if (showBottomSheet.value) {
            EditBudgetBottomSheet(
                budget = detailsState.budget!!,
                onSave = { amount: String, note: String, impact: String, budgetOptions: MutableList<BudgetOption> ->
                    detailsViewModel.submitBudgetEdit(
                        detailsState.budget.id,
                        amount,
                        note,
                        impact,
                        budgetOptions
                    )
                    showBottomSheet.value = false
                },
                onClose = {
                    showBottomSheet.value = false
                }
            )

        }
    }
}

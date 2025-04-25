package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetViewModel

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
    val context = LocalContext.current

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
            Text("Total Budget: ₦${detailsState.budget?.amount}", style = MaterialTheme.typography.bodyMedium)
            Text("Impact: ${detailsState.budget?.impact}", style = MaterialTheme.typography.bodyMedium)
            Text("Note: ${detailsState.budget?.budgetNote}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Budget Options", style = MaterialTheme.typography.titleMedium)

            detailsState.budgetOptions.forEachIndexed { index, option ->
                Spacer(modifier = Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Project: ${option.optionProjectName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("Description: ${option.optionDescription}", style = MaterialTheme.typography.bodyMedium)
                        Text("Amount: ₦${option.optionAmount}", style = MaterialTheme.typography.bodyMedium)
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
                                        detailsViewModel.onEvent(BudgetDetailsEvent.VoteOption(option.optionId))
                                    }
                                )
                            } else if (votedOptionId == option.optionId) {
                                Text("You voted for this option", color = Color.Green)
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
                    modifier = Modifier.fillMaxWidth().background(Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))

                CustomButton(
                    text = "Edit Budget Details",
                    onClick = {
                        navController.navigate("edit_budget/$budgetId")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

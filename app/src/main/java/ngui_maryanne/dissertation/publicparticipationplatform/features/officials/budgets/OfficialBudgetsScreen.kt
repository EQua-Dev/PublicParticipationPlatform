package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation.PetitionEvent
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfficialBudgetsScreen(
    viewModel: OfficialBudgetViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val state = viewModel.uiState.value
//    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(state.navigateToCreateBudget) {
        if (state.navigateToCreateBudget) {
            navController.navigate("create_budget")
            viewModel.onEvent(OfficialBudgetEvent.OnNavigateDone)
        }
    }

    Scaffold(
//        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Budgets") })
        },
        floatingActionButton = {
            if (state.currentUserRole!!.lowercase() == "official") {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.CreateNewBudgetScreen.route)
//                        viewModel.onEvent(OfficialBudgetEvent.OnFabClicked)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Budget")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.budgets.isEmpty()) {
                Text(
                    text = "No budgets available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.budgets) { budget ->
                        BudgetItemCard(budget = budget, onClick = {
                            navController.navigate(
                                Screen.BudgetDetailsScreen.route.replace(
                                    "{budgetId}",
                                    budget.id
                                )
                            )
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetItemCard(budget: Budget, onClick : () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Budget No: ${budget.budgetNo}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Amount: KSH${budget.amount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Impact: ${budget.impact}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Note: ${budget.budgetNote}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Options: ${budget.budgetOptions.size}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}


package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.SuperAdminBottomBarScreen

@Composable
fun SuperAdminDashboardScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: SuperAdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.onEvent(SuperAdminDashboardEvent.LoadDashboardData)
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = modifier.fillMaxSize()
        ) {
            item {
                DashboardCard(
                    title = "Citizens",
                    count = state.citizensCount,
                    onClick = {
                        navController.navigate(
                            SuperAdminBottomBarScreen.People.route.replace(
                                "{selectedIndex}",
                                "0"
                            )
                        ) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(SuperAdminBottomBarScreen.Dashboard.route) {
                                saveState = true
                            }
                        }
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Officials",
                    count = state.officialsCount,
                    onClick = {
                        navController.navigate(
                            SuperAdminBottomBarScreen.People.route.replace(
                                "{selectedIndex}",
                                "1"
                            )
                        ) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(SuperAdminBottomBarScreen.Dashboard.route) {
                                saveState = true
                            }
                        }
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Policies",
                    count = state.policiesCount,
                    onClick = {
                        navController.navigate(Screen.CitizenPolicies.route)
//                        viewModel.onEvent(SuperAdminDashboardEvent.CardClicked(DashboardCardType.Policies))
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Polls",
                    count = state.pollsCount,
                    onClick = {
                        navController.navigate(Screen.CitizenPolls.route)
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Budgets",
                    count = state.budgetsCount,
                    onClick = {
                        navController.navigate(Screen.CitizenParticipatoryBudget.route)
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Petitions",
                    count = state.petitionsCount,
                    onClick = {
                        navController.navigate(Screen.CitizenPetitions.route)
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


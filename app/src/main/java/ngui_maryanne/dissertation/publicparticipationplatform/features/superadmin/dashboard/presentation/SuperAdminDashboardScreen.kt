package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.R
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
                    icon = R.drawable.ic_citizens,
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
                    icon = R.drawable.ic_profile,
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
                    icon = R.drawable.ic_policies,
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
                    icon = R.drawable.ic_polls,
                    onClick = {
                        navController.navigate(Screen.CitizenPolls.route)
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Budgets",
                    count = state.budgetsCount,
                    icon = R.drawable.ic_budget,
                    onClick = {
                        navController.navigate(Screen.CitizenParticipatoryBudget.route)
                    }
                )
            }
            item {
                DashboardCard(
                    title = "Petitions",
                    count = state.petitionsCount,
                    icon = R.drawable.ic_petitions,
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
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(durationMillis = 800),
        label = "countAnimation"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f) // Optional: square card
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title and icon (Top-Start)
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "$title Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Animated Count (Bottom-End)
            Text(
                text = animatedCount.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}


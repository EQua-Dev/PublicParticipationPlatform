package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.notification.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getTimeAgo

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(), // Or pass manually
    navController: NavHostController,
//    onNotificationClick: (AppNotification) -> Unit
) {
    val notifications = viewModel.notifications
    val error by viewModel.error

    LaunchedEffect(Unit) {
        viewModel.startListeningForNotifications()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Notifications", style = MaterialTheme.typography.headlineMedium)

        if (notifications.isEmpty()) {
            Text("No notifications yet.", modifier = Modifier.padding(top = 24.dp))
        } else {
            LazyColumn {
                val sortedNotifications = notifications.sortedByDescending { it.dateCreated.toLongOrNull() ?: 0L }

                items(sortedNotifications) { notification ->
                    NotificationItem(notification, onClick = {
                        when (notification.type) {
                            NotificationTypes.BUDGET -> {
                                navController.navigate(
                                    Screen.BudgetDetailsScreen.route.replace(
                                        "{budgetId}",
                                        notification.typeId
                                    )
                                )
                            }

                            NotificationTypes.POLL -> {
                                navController.navigate(
                                    Screen.PollDetailsScreen.route.replace(
                                        "{pollId}",
                                        notification.typeId
                                    )
                                )
                            }

                            NotificationTypes.PETITION -> {
                                navController.navigate(
                                    Screen.CitizenPetitionDetailsScreen.route.replace(
                                        "{petitionId}",
                                        notification.typeId
                                    )
                                )
                            }

                            NotificationTypes.POLICY -> {
                                navController.navigate(
                                    Screen.CitizenPolicyDetailsScreen.route.replace(
                                        "{policyId}",
                                        notification.typeId
                                    )
                                )
                            }

                            else -> {

                            }

                        }
                    })
                    Divider()
                }
            }
        }

        error?.let {
            Text("Error: $it", color = Color.Red)
        }
    }
}


@Composable
fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.type.name.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = getTimeAgo(notification.dateCreated),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodySmall
            )
        }

    }
}

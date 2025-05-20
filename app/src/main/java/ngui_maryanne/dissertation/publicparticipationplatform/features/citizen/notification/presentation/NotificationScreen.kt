package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.notification.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.FullScreenLoading
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getTimeAgo
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    navController: NavHostController
) {
//    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.startListeningForNotifications()
    }

    // Handle errors
    viewModel.error?.let { error ->
        LaunchedEffect(error) {/*
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(NotificationEvent.ErrorShown)*/
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                viewModel.loading.value -> FullScreenLoading()
                viewModel.notifications.isEmpty() -> EmptyNotificationsState()
                else -> NotificationList(viewModel.notifications, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationList(
    notifications: List<AppNotification>,
    navController: NavHostController
) {
    val groupedNotifications = remember(notifications) {
        notifications.groupBy {
            Instant.ofEpochMilli(it.dateCreated.toLongOrNull() ?: 0L)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSortedMap(reverseOrder())
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        groupedNotifications.forEach { (date, dailyNotifications) ->
            val dateString =
                when {
                    date.isEqual(LocalDate.now()) -> "Today"
                    date.isEqual(LocalDate.now().minusDays(1)) -> "Yesterday"
                    else -> DateTimeFormatter.ofPattern("MMMM d, yyyy").format(date)
                }


            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            items(dailyNotifications.sortedByDescending { it.dateCreated.toLongOrNull() ?: 0L }) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = { handleNotificationClick(notification, navController) }
                )
                Divider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val icon = remember(notification.type) {
        when (notification.type) {
            NotificationTypes.BUDGET -> R.drawable.ic_budget
            NotificationTypes.POLL -> R.drawable.ic_polls
            NotificationTypes.PETITION -> R.drawable.ic_petitions
            NotificationTypes.POLICY -> R.drawable.ic_policies
            else -> R.drawable.ic_dashboard
        }
    }


    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceColor = MaterialTheme.colorScheme.onSurface

    val iconColor = remember(notification.type) {
        when (notification.type) {
            NotificationTypes.BUDGET -> primaryColor
            NotificationTypes.POLL -> secondaryColor
            NotificationTypes.PETITION -> tertiaryColor
            NotificationTypes.POLICY -> errorColor
            else -> surfaceColor
        }
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = notification.type.name,
                tint = iconColor,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .padding(8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.type.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getTimeAgo(notification.dateCreated),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

          /*  if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            }*/
        }
    }
}

@Composable
private fun EmptyNotificationsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No notifications yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

private fun handleNotificationClick(notification: AppNotification, navController: NavHostController) {
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
        else -> Unit
    }
}

private fun getTimeAgo(timestamp: String): String {
    val now = System.currentTimeMillis()
    val time = timestamp.toLongOrNull() ?: return ""
    val diff = now - time

    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
        else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(time))
    }
}
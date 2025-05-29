package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.components.CustomButton
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.BackgroundAnimations
import ngui_maryanne.dissertation.publicparticipationplatform.ui.components.LoadingDialog
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.KenyaGreen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe

@Composable
fun CitizenHomeScreen(
    navController: NavHostController,
    viewModel: CitizenHomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val announcements = state.announcements

    // Define color palette with sophisticated hierarchy
    val backgroundColor = Color(0xFFF8F9FA) // Light neutral background
    val surfaceColor = MaterialTheme.colorScheme.surface // Pure white for surfaces
    val primaryText = Color(0xFF2C3E50) // Deep blue-gray for primary text
    val secondaryText = Color(0xFF5D6D7E) // Lighter blue-gray for secondary text
    val accentPrimary = MaterialTheme.colorScheme.primary


    // Service-specific subtle accent colors
    val policyColor = Color(0xFF5E81AC) // Soft blue for policies
    val pollsColor = Color(0xFF5E9C76) // Soft green for polls
    val petitionsColor = Color(0xFF8C6D62) // Warm brown for petitions
    val budgetColor = Color(0xFF7D6B91) // Soft purple for budget

    LaunchedEffect(key1 = state) {
        if (state.logout) {
            navController.navigate(Screen.InitRoleTypeScreen.route)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CitizenHomeTopBar(
                citizen = state.citizen,
                notifications = state.notifications,
                onProfileClick = { navController.navigate(Screen.CitizenProfileScreen.route) },
                onNotificationsClick = { navController.navigate(Screen.NotificationScreen.route) },
                onLogout = { openDialog.value = true },
                topBarBackgroundColor = surfaceColor,
                topBarContentColor = primaryText,
                topBarAccentColor = accentPrimary
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Content based on state
            when {
                state.isLoading -> LoadingDialog(loadingText = stringResource(R.string.loading_your_dashboard))
                !state.isApproved -> AwaitingApprovalScreen(
                    contentColor = primaryText,
                    accentColor = accentPrimary
                )

                else -> ApprovedCitizenHome(
                    paddingValues = paddingValues,
                    navController = navController,
                    announcements = announcements,
                    contentColor = primaryText,
                    secondaryText = secondaryText,
                    backgroundColor = backgroundColor,
                    surfaceColor = surfaceColor,
                    policyColor = policyColor,
                    pollsColor = pollsColor,
                    petitionsColor = petitionsColor,
                    budgetColor = budgetColor
                )
            }
        }
    }

    if (openDialog.value) {
        ElegantAlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = stringResource(R.string.logout),
            message = stringResource(R.string.do_you_want_to_logout),
            onConfirm = { viewModel.onEvent(CitizenHomeEvent.Logout) },
            onDismiss = { openDialog.value = false },
            dialogBackgroundColor = surfaceColor,
            dialogContentColor = primaryText,
            dialogAccentColor = accentPrimary
        )
    }
}

@Composable
fun ElegantAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dialogBackgroundColor: Color,
    dialogContentColor: Color,
    dialogAccentColor: Color
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = dialogBackgroundColor,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = dialogContentColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    color = dialogContentColor.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = dialogContentColor.copy(alpha = 0.8f)
                        )
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = dialogAccentColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(id = R.string.logout))
                    }
                }
            }
        }
    }
}

@Composable
fun AwaitingApprovalScreen(
    contentColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.HourglassEmpty,
                contentDescription = stringResource(R.string.awaiting_approval),
                modifier = Modifier.size(80.dp),
                tint = accentColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            stringResource(R.string.awaiting_approval),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = contentColor,
                letterSpacing = 0.25.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(R.string.your_account_is_pending_approval_you_ll_gain_access_to_all_features_once_approved),
            style = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = contentColor.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        OutlinedButton(
            onClick = { /* Navigate to app guide */ },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = accentColor
            ),
            border = BorderStroke(1.dp, accentColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.learn_about_the_app),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ApprovedCitizenHome(
    paddingValues: PaddingValues,
    navController: NavHostController,
    announcements: MutableList<Announcement>,
    contentColor: Color,
    secondaryText: Color,
    backgroundColor: Color,
    surfaceColor: Color,
    policyColor: Color,
    pollsColor: Color,
    petitionsColor: Color,
    budgetColor: Color
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Announcements section
        if (announcements.isNotEmpty()) {
            Text(
                text = stringResource(R.string.announcements),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = secondaryText,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            AnnouncementsCarousel(
                announcements = announcements,
                onAnnouncementClick = { announcement ->
                    when (announcement.type) {
                        NotificationTypes.POLICY -> navController.navigate(Screen.CitizenPolicies.route)
                        NotificationTypes.POLL -> navController.navigate(Screen.CitizenPolls.route)
                        NotificationTypes.PETITION -> navController.navigate(Screen.CitizenPetitions.route)
                        else -> navController.navigate(Screen.CitizenParticipatoryBudget.route)
                    }
                },
                carouselBackgroundColor = surfaceColor,
                carouselContentColor = contentColor,
                policyColor = policyColor,
                pollsColor = pollsColor,
                petitionsColor = petitionsColor,
                budgetColor = budgetColor
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Services section heading
        Text(
            text = stringResource(R.string.services),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = secondaryText,
                letterSpacing = 1.2.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Service cards grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            val services = listOf(
                ServiceItem(R.drawable.ic_policies,
                    context.getString(R.string.policies), policyColor) {
                    navController.navigate(Screen.CitizenPolicies.route)
                },
                ServiceItem(R.drawable.ic_polls, context.getString(R.string.polls), pollsColor) {
                    navController.navigate(Screen.CitizenPolls.route)
                },
                ServiceItem(R.drawable.ic_petitions,
                    context.getString(R.string.petitions), petitionsColor) {
                    navController.navigate(Screen.CitizenPetitions.route)
                },
                ServiceItem(R.drawable.ic_budget, context.getString(R.string.budget), budgetColor) {
                    navController.navigate(Screen.CitizenParticipatoryBudget.route)
                }
            )

            items(services.size) { index ->
                ActionCard(
                    icon = services[index].icon,
                    label = services[index].label,
                    accentColor = services[index].color,
                    onClick = services[index].onClick,
                    cardBackgroundColor = surfaceColor,
                    cardContentColor = contentColor
                )
            }
        }
    }
}

data class ServiceItem(
    val icon: Int,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ActionCard(
    icon: Int,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    cardBackgroundColor: Color,
    cardContentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,  // More subtle animation
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_scale"
    )
    val isDark = isSystemInDarkTheme()


    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(1f)
            .clickable { onClick() }
            .graphicsLayer {
                scaleX = if (isDark) scale else 1f
                scaleY = if (isDark) scale else 1f
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        radius = 250f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(id = icon),
                        contentDescription = label,
                        modifier = Modifier.size(28.dp),
                        tint = accentColor
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = cardContentColor
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenHomeTopBar(
    citizen: Citizen?,
    notifications: MutableList<AppNotification>,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLogout: () -> Unit,
    topBarBackgroundColor: Color,
    topBarContentColor: Color,
    topBarAccentColor: Color
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile image or icon
                IconButton(onClick = onProfileClick) {
                    if (citizen?.profileImage?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .border(
                                    width = 1.dp,
                                    color = topBarAccentColor.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .padding(1.dp)
                        ) {
                            AsyncImage(
                                model = citizen.profileImage,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    color = topBarAccentColor.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = topBarAccentColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        "Wajibika,",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            color = topBarContentColor.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = citizen?.firstName?.let { stringResource(R.string.welcome, it) } ?: "Citizen",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = topBarContentColor
                        )
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topBarBackgroundColor
        ),
        actions = {
            // Notifications Icon with Badge
            IconButton(onClick = onNotificationsClick) {
                BadgedBox(
                    badge = {
                        if (notifications.isNotEmpty()) {
                            Badge(
                                containerColor = topBarAccentColor
                            ) {
                                Text(
                                    notifications.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = topBarAccentColor
                    )
                }
            }

            // Logout button
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = topBarAccentColor
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementsCarousel(
    announcements: List<Announcement>,
    onAnnouncementClick: (Announcement) -> Unit,
    carouselBackgroundColor: Color,
    carouselContentColor: Color,
    policyColor: Color,
    pollsColor: Color,
    petitionsColor: Color,
    budgetColor: Color
) {
    val pagerState = rememberPagerState(pageCount = { announcements.size })

    LaunchedEffect(Unit) {
        // Auto-slide logic with longer delay
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % announcements.size
            pagerState.animateScrollToPage(
                nextPage,
                animationSpec = tween(600, easing = EaseOutQuad)
            )
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { pageIndex ->
            val announcement = announcements[pageIndex]
            val announcementColor = when (announcement.type) {
                NotificationTypes.POLICY -> policyColor
                NotificationTypes.POLL -> pollsColor
                NotificationTypes.PETITION -> petitionsColor
                else -> budgetColor
            }

            AnnouncementCard(
                announcement = announcement,
                onClick = { onAnnouncementClick(announcement) },
                cardBackgroundColor = carouselBackgroundColor,
                cardContentColor = carouselContentColor,
                cardAccentColor = announcementColor
            )
        }

        // Page indicator dots
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(announcements.size) { iteration ->
                val announcement = announcements[iteration]
                val dotColor = when (announcement.type) {
                    NotificationTypes.POLICY -> policyColor
                    NotificationTypes.POLL -> pollsColor
                    NotificationTypes.PETITION -> petitionsColor
                    else -> budgetColor
                }

                val color = if (pagerState.currentPage == iteration) {
                    dotColor
                } else {
                    carouselContentColor.copy(alpha = 0.2f)
                }

                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = width,
                            height = 4.dp
                        )
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementCard(
    announcement: Announcement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cardBackgroundColor: Color,
    cardContentColor: Color,
    cardAccentColor: Color
) {
    val iconRes = when (announcement.type) {
        NotificationTypes.POLICY -> R.drawable.ic_policies
        NotificationTypes.POLL -> R.drawable.ic_polls
        NotificationTypes.PETITION -> R.drawable.ic_petitions
        else -> R.drawable.ic_budget
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { /* Optional: Handle long click */ }
            ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = cardAccentColor.copy(alpha = 0.15f)
        )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left icon column
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(72.dp)
                    .background(
                        color = cardAccentColor.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Announcement icon",
                    modifier = Modifier.size(32.dp),
                    tint = cardAccentColor
                )
            }

            // Content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category badge
                    Surface(
                        color = cardAccentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = when (announcement.type) {
                                NotificationTypes.POLICY -> stringResource(id = R.string.policy)
                                NotificationTypes.POLL -> stringResource(id = R.string.poll)
                                NotificationTypes.PETITION -> stringResource(id = R.string.petition)
                                else -> stringResource(id = R.string.budget)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = cardAccentColor,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // Title
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cardContentColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description
                    Text(
                        text = announcement.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 12.sp,
                            color = cardContentColor.copy(alpha = 0.7f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Footer with date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = cardAccentColor.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = HelpMe.getDate(announcement.createdAt.toLong(), "dd MMM"),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 10.sp,
                            color = cardContentColor.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.holder

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.awesomenessstudios.schoolprojects.criticalthinkingappforkids.providers.LocalNavHost
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.CitizenHomeScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.notification.presentation.NotificationsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails.PetitionDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation.CitizenPetitionsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails.CitizenPolicyDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation.CitizenPoliciesScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails.PollDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.CitizenPollsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.CitizenProfileScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.audit.presentation.CitizenAuditScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.forgotpassword.ForgotPasswordScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.login.LoginScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup.CitizenRegistrationScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.common.auth.presentation.signup.CitizenRegistrationVerification
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.OfficialHomeScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.CreateBudgetScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails.BudgetDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.createpoll.CreatePollScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.SuperAdminHomeScreen
import ngui_maryanne.dissertation.publicparticipationplatform.navigation.Screen
import ngui_maryanne.dissertation.publicparticipationplatform.utils.Common.mAuth
import ngui_maryanne.dissertation.publicparticipationplatform.utils.getDp

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolderScreen(
    onLanguageChange: (String) -> Unit,
    onStatusBarColorChange: (color: Color) -> Unit,
    holderViewModel: HolderViewModel = hiltViewModel(),
) {

    val state by holderViewModel.state.collectAsState(initial = HolderUiState())
    LaunchedEffect(state.selectedLanguage) {
        // Apply language change globally
//        currentLocale = state.selectedLanguage.code
//        onLanguageChange(state.selectedLanguage.code)

    }
    val TAG = "HolderScreen"
    /*  val destinations = remember {
          listOf(Screen.Home, Screen.Notifications, Screen.Bookmark, Screen.Profile)
      }*/

    /** Our navigation controller that the MainActivity provides */
    val controller = LocalNavHost.current

    /** The current active navigation route */
    val currentRouteAsState = getActiveRoute(navController = controller)

    /** The current logged user, which is null by default */

    /** The main app's scaffold state */
    val scaffoldState = rememberBottomSheetScaffoldState()

    /** The coroutine scope */
    val scope = rememberCoroutineScope()

    /** Dynamic snack bar color */
    val (snackBarColor, setSnackBarColor) = remember {
        mutableStateOf(Color.White)
    }

    /** SnackBar appear/disappear transition */
    val snackBarTransition = updateTransition(
        targetState = scaffoldState.snackbarHostState,
        label = "SnackBarTransition"
    )

    /** SnackBar animated offset */
    val snackBarOffsetAnim by snackBarTransition.animateDp(
        label = "snackBarOffsetAnim",
        transitionSpec = {
            TweenSpec(
                durationMillis = 300,
                easing = LinearEasing,
            )
        }
    ) {
        when (it.currentSnackbarData) {
            null -> {
                100.getDp()
            }

            else -> {
                0.getDp()
            }
        }
    }

    Box {
        /** Cart offset on the screen */
        val (cartOffset, setCartOffset) = remember {
            mutableStateOf(IntOffset(0, 0))
        }
        ScaffoldSection(
            controller = controller,
            scaffoldState = scaffoldState,
            onStatusBarColorChange = onStatusBarColorChange,
            onNavigationRequested = { route, removePreviousRoute ->
                if (removePreviousRoute) {
                    controller.popBackStack()
                }
                controller.navigate(route)
            },
            onBackRequested = {
                controller.popBackStack()
            },
            onAuthenticated = { userType ->
                var navRoute = ""
                when (userType) {
                    /*Common.UserTypes.STUDENT.userType -> navRoute = Screen.StudentLanding.route
                    Common.UserTypes.LECTURER.userType -> navRoute =
                        Screen.LecturerLandingScreen.route*/
                }
                controller.navigate(navRoute) {
                    /* popUpTo(Screen.Login.route) {
                         inclusive = true
                     }*/
                }
            },
            onAccountCreated = {
                //nav to register courses
                /*controller.navigate(Screen.CourseRegistration.route) {
                    popUpTo(Screen.Signup.route) {
                        inclusive = true
                    }
                }*/
            },
            onNewScreenRequest = { route, patientId ->
                controller.navigate(route.replace("{patientId}", "$patientId"))
            },
            onLogoutRequested = {
                mAuth.signOut()
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSection(
    controller: NavHostController,
    scaffoldState: BottomSheetScaffoldState,
    onStatusBarColorChange: (color: Color) -> Unit,
    onNavigationRequested: (route: String, removePreviousRoute: Boolean) -> Unit,
    onBackRequested: () -> Unit,
    onAuthenticated: (userType: String) -> Unit,
    onAccountCreated: () -> Unit,
    onNewScreenRequest: (route: String, id: String?) -> Unit,
    onLogoutRequested: () -> Unit
) {
    Scaffold(
        //scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier.weight(1f),
                navController = controller,
                startDestination = Screen.InitRoleTypeScreen.route
            ) {
                composable(Screen.InitRoleTypeScreen.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    InitScreen(
                        onRoleSelected = { _ ->
                            controller.navigate(Screen.Login.route)
                        },
                    )
                }
                composable(Screen.CitizenRegistration.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenRegistrationScreen(
                        onRegistrationSuccess = { controller.navigate(Screen.CitizenHome.route) },
                    )
                }
                composable(Screen.Login.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    LoginScreen(
                        onLoginSuccess = { userRole ->
                            when (userRole) {
                                UserRole.SUPERADMIN.name -> {
                                    controller.navigate(Screen.SuperAdminHome.route)
                                }

                                UserRole.OFFICIAL.name -> {
                                    controller.navigate(Screen.OfficialHomeScreen.route)
                                }

                                else -> {
                                    controller.navigate(Screen.CitizenHome.route)
                                }
                            }
//                            if (userRole === UserRole.SUPERADMIN.name) {
//                                controller.navigate(Screen.SuperAdminHome.route)
//                            } else {
//                                controller.navigate(Screen.CitizenHome.route)
//                            }
                        },
                        onForgotPasswordClicked = { controller.navigate(Screen.ForgotPassword.route) },
                        onRegisterClicked = { _ ->
                            controller.navigate(Screen.CitizenRegistration.route)
                        }
                    )
                }
                composable(Screen.ForgotPassword.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    ForgotPasswordScreen(
                        onPasswordResetSuccess = { controller.navigate(Screen.Login.route) }
                    )
                }
                composable(Screen.SuperAdminHome.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    SuperAdminHomeScreen(
                        baseNavHostController = controller,
                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.CitizenRegistration.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenRegistrationScreen(
                        onRegistrationSuccess = { controller.navigate(Screen.Login.route) },
                    )
                }
                composable(Screen.CitizenHome.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenHomeScreen(
                        navController = controller,
//                        onNavigationRequested = onNavigationRequested,
                    )
                }

                composable(Screen.CitizenPolicies.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenPoliciesScreen(
                        navController = controller,
//                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.CitizenPolls.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenPollsScreen(
                        navController = controller,
//                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.CitizenPetitions.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenPetitionsScreen(
                        navController = controller,
//                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.CitizenParticipatoryBudget.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    OfficialBudgetsScreen(
                        navController = controller,
//                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.OfficialHomeScreen.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    OfficialHomeScreen(
                        baseNavHostController = controller,
                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.CitizenRegistrationVerificationScreen.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    CitizenRegistrationVerification(
//                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(
                    Screen.CitizenPolicyDetailsScreen.route,
                    arguments = listOf(
                        navArgument(name = "policyId") { type = NavType.StringType }
                    ),
                ) {
                    val policyId = it.arguments?.getString("policyId")

                    CitizenPolicyDetailsScreen(
                        policyId = policyId!!,
                        navController = controller
                    )
                }
                composable(
                    Screen.PollDetailsScreen.route,
                    arguments = listOf(
                        navArgument(name = "pollId") { type = NavType.StringType }
                    ),
                ) {
                    val pollId = it.arguments?.getString("pollId")

                    PollDetailsScreen(
                        pollId = pollId!!,
                        navController = controller
                    )
                }

                composable(
                    Screen.CitizenPetitionDetailsScreen.route,
                    arguments = listOf(
                        navArgument(name = "petitionId") { type = NavType.StringType }
                    ),
                ) {
                    val petitionId = it.arguments?.getString("petitionId")

                    PetitionDetailsScreen(
                        petitionId = petitionId!!,
                        navHostController = controller
                    )
                }
                composable(
                    Screen.BudgetDetailsScreen.route,
                    arguments = listOf(
                        navArgument(name = "budgetId") { type = NavType.StringType }
                    ),
                ) {
                    val budgetId = it.arguments?.getString("budgetId")

                    BudgetDetailsScreen(
                        budgetId = budgetId!!,
                        navController = controller
                    )
                }
                composable(
                    Screen.CitizenProfileScreen.route,
                ) {
                    CitizenProfileScreen(
                        navController = controller
                    )
                }
                composable(
                    Screen.AuditLogScreen.route
                ) {
                    CitizenAuditScreen(
                        navController = controller
                    )
                }
                composable(Screen.CreatePollScreen.route) {
                    CreatePollScreen(
                        navController = controller
                    )
                }
                composable(Screen.CreateNewBudgetScreen.route) {
                    CreateBudgetScreen(
                        navController = controller
                    )

                }
                composable(Screen.NotificationScreen.route) {
                    NotificationsScreen(
                        navController = controller
                    )

                }
            }
        }
    }
}

/**
 * A function that is used to get the active route in our Navigation Graph , should return the splash route if it's null
 */
@Composable
fun getActiveRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "splash"
}

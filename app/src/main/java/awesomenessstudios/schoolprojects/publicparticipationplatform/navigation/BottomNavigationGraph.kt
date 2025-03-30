/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package awesomenessstudios.schoolprojects.publicparticipationplatform.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.budgets.OfficialBudgetsScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.citizens.OfficialCitizensScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.petitions.OfficialPetitionsScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.OfficialPoliciesScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.policies.createpolicy.CreatePolicyScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.polls.OfficialPollsScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.profile.OfficialProfileScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.audit.presentation.SuperAdminAuditScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.dashboard.presentation.SuperAdminDashboardScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.citizens.presentation.CreateCitizenScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation.CreateOfficialScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.presentation.SuperAdminPeopleScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.profile.presentation.SuperAdminProfileScreen
import awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.publicparticipation.presentation.SuperAdminPublicParticipationScreen


@Composable
fun OfficialBottomNavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = OfficialBottomBarScreen.Policies.route
    ) {
        composable(
            route = OfficialBottomBarScreen.Policies.route
        ) {
            OfficialPoliciesScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Polls.route
        ) {
            OfficialPollsScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Budget.route
        ) {
            OfficialBudgetsScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Petitions.route
        ) {
            OfficialPetitionsScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Profile.route
        ) {
            OfficialProfileScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Citizens.route
        ) {
            OfficialCitizensScreen(navController = navController, onAddCitizenClick = {
                navController.navigate(Screen.CreateCitizenScreen.route)
            })
        }
        composable(Screen.CreateCitizenScreen.route) {
            CreateCitizenScreen(
//                        onNavigationRequested = onNavigationRequested,
            )
        }
        composable(Screen.CreateCreatePolicyScreen.route) {
            CreatePolicyScreen(
                navController = navController
            )
        }
        /*composable(
            Screen.FeesSemester.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            FeesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                //onBackRequested = onBackRequested,

            )
        }
        composable(
            Screen.DuesSemesterScreen.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            DuesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                onPay = {}
                //onBackRequested = onBackRequested,

            )
        }*/
    }

}

@Composable
fun SuperAdminBottomNavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = SuperAdminBottomBarScreen.Dashboard.route
    ) {
        composable(
            route = SuperAdminBottomBarScreen.Dashboard.route
        ) {
            SuperAdminDashboardScreen(navController = navController)
        }
        composable(
            route = SuperAdminBottomBarScreen.PublicParticipation.route
        ) {
            SuperAdminPublicParticipationScreen(navController = navController)
        }
        composable(
            route = SuperAdminBottomBarScreen.People.route
        ) {
            SuperAdminPeopleScreen(navController = navController)
        }
        composable(
            route = SuperAdminBottomBarScreen.Audit.route
        ) {
            SuperAdminAuditScreen(navController = navController)
        }
        composable(
            route = SuperAdminBottomBarScreen.Profile.route
        ) {
            SuperAdminProfileScreen(navController = navController)
        }
        composable(Screen.CreateCitizenScreen.route) {
            CreateCitizenScreen(
//                        onNavigationRequested = onNavigationRequested,
            )
        }
        composable(Screen.CreateOfficialScreen.route) {
            CreateOfficialScreen(
//                        onNavigationRequested = onNavigationRequested,
            )
        }
        /*
        composable(
            Screen.FeesSemester.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            FeesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                //onBackRequested = onBackRequested,

            )
        }
        composable(
            Screen.DuesSemesterScreen.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            DuesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                onPay = {}
                //onBackRequested = onBackRequested,

            )
        }*/
    }

}
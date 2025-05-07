/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package ngui_maryanne.dissertation.publicparticipationplatform.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.petitiondetails.PetitionDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation.CitizenPetitionsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.policydetails.CitizenPolicyDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.policies.presentation.CitizenPoliciesScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.polldetails.PollDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.polls.presentation.CitizenPollsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.CreateBudgetScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.OfficialBudgetsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails.BudgetDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens.OfficialCitizensScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.OfficialPoliciesScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.createpolicy.CreatePolicyScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.policies.policydetails.OfficialPolicyDetailsScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.polls.createpoll.CreatePollScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.OfficialProfileScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile.audit.presentation.OfficialAuditScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.audit.presentation.SuperAdminAuditScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation.SuperAdminDashboardScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation.CreateCitizenScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation.CreateOfficialScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.officialdetail.OfficialDetailScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.presentation.SuperAdminPeopleScreen
import ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.profile.presentation.SuperAdminProfileScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfficialBottomNavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = OfficialBottomBarScreen.Policies.route
    ) {
        composable(
            route = OfficialBottomBarScreen.Policies.route
        ) {
            OfficialPoliciesScreen(navController = navController, onPolicyClicked = { policyId ->
                navController.navigate(
                    Screen.PolicyDetailsScreen.route.replace(
                        "{policyId}",
                        policyId
                    )
                )
            })
        }
        composable(
            route = OfficialBottomBarScreen.Polls.route
        ) {
            CitizenPollsScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Budget.route
        ) {
            OfficialBudgetsScreen(navController = navController)
        }
        composable(
            route = OfficialBottomBarScreen.Petitions.route
        ) {
            CitizenPetitionsScreen(navController = navController)
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
        composable(Screen.CreatePolicyScreen.route) {
            CreatePolicyScreen(
                navController = navController
            )
        }
        composable(
            Screen.PolicyDetailsScreen.route,
            arguments = listOf(
                navArgument(name = "policyId") { type = NavType.StringType }
            ),
        ) {
            val policyId = it.arguments?.getString("policyId")

            OfficialPolicyDetailsScreen(
                policyId!!,
                navController = navController
            )
        }
        composable(Screen.CreatePollScreen.route) {
            CreatePollScreen(
                navController = navController
            )
        }
            composable(Screen.CreateNewBudgetScreen.route) {
                CreateBudgetScreen(
                    navController = navController
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
                navController = navController
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
                navHostController = navController
            )
        }
        composable(
            Screen.AuditLogScreen.route
        ) {
            OfficialAuditScreen(
                navController = navController
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

@RequiresApi(Build.VERSION_CODES.O)
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
            route = SuperAdminBottomBarScreen.People.route,
            arguments = listOf(
                navArgument(name = "selectedIndex") { type = NavType.IntType }
            ),
        ) {
            val selectedIndex = it.arguments?.getInt("selectedIndex")
            SuperAdminPeopleScreen(navController = navController, selectedTab = selectedIndex ?: 0)
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
        composable(
            route = Screen.CitizenPolicies.route
        ) {
            CitizenPoliciesScreen(navController = navController)
        }

        composable(Screen.CitizenPolls.route) {

            CitizenPollsScreen(
                navController = navController,
//                        onNavigationRequested = onNavigationRequested,
            )
        }
        composable(Screen.CitizenPetitions.route) {
            CitizenPetitionsScreen(
                navController = navController,
//                        onNavigationRequested = onNavigationRequested,
            )
        }
        composable(Screen.CitizenParticipatoryBudget.route) {
            OfficialBudgetsScreen(
                navController = navController,
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
                navController = navController
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
                navController = navController
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
                navHostController = navController
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
                navController = navController
            )
        }

        composable(
            Screen.OfficialDetailsScreen.route,
            arguments = listOf(
                navArgument(name = "officialId") { type = NavType.StringType }
            ),
        ) {
            val officialId = it.arguments?.getString("officialId")

            OfficialDetailScreen(
                officialId = officialId!!,
                navController = navController
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

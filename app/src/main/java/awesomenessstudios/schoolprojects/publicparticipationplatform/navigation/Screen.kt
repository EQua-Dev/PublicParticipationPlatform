package awesomenessstudios.schoolprojects.publicparticipationplatform.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import awesomenessstudios.schoolprojects.publicparticipationplatform.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int? = null,
) {
    object Splash : Screen(
        route = "splash",
    )

    object InitRoleTypeScreen : Screen(
        route = "role_type",
    )

    object CitizenRegistration : Screen(
        route = "citizen_registration",
        title = R.string.citizen_registration,
    )

    object Login : Screen(
        route = "login",
        title = R.string.login,
    )

    object ForgotPassword : Screen(
        route = "forgot_password",
        title = R.string.forgot_password,
    )

    object SuperAdminHome : Screen(
        route = "super_admin_home",
        title = R.string.super_admin_home,
    )
    object CitizenHome : Screen(
        route = "citizen_home",
        title = R.string.citizen_home,
    )
    object OfficialHomeScreen : Screen(
        route = "official_home_screen",
        title = R.string.official_home,
    )
    object CitizenRegistrationVerificationScreen : Screen(
        route = "citizen_registration_verification_screen",
        title = R.string.citizen_registration_verification_screen,
    )
    object CreateOfficialScreen : Screen(
        route = "create_official_screen",
        title = R.string.create_official_screen,
    )
    object CreateCitizenScreen : Screen(
        route = "create_citizen_screen",
        title = R.string.create_citizen_screen,
    )
    object CreatePolicyScreen : Screen(
        route = "create_policy_screen",
        title = R.string.create_policy_screen,
    )
    object PolicyDetailsScreen : Screen(
        route = "policy_details_screen/{policyId}",
        title = R.string.policy_details_screen,
    )
    object CreatePollScreen : Screen(
        route = "create_poll_screen",
        title = R.string.create_poll_screen,
    )
    /*
        object ChildHome : Screen(
            route = "childhome/{childId}",
            title = R.string.child_home,
        )

        object CategoryOverview : Screen(
            route = "categoryoverview/{childId}/{category}/{childStage}",
            title = R.string.category_overview,
        )

        object ActivityTypeOverview : Screen(
            route = "activitytypeoverview/{activityTypeKey}/{childId}/{category}/{childStage}",
            title = R.string.activity_type_overview,
        )

        object ActivityTypeRule : Screen(
            route = "activitytyperule/{childId}/{categoryKey}/{childStage}/{activityTypeKey}/{selectedDifficulty}/{lastScore}/{lastPlayed}}",
            title = R.string.activity_type_rule,
        )

        object Quiz : Screen(
            route = "quiz/{childId}/{categoryKey}/{childStage}/{difficultyLevel}",
            title = R.string.quiz,
        )

        object Game : Screen(
            route = "game/{childId}/{categoryKey}/{difficultyLevel}/{childStage}",
            title = R.string.game,
        )

        object Video : Screen(
            route = "video/{childStage}/{category}",
            title = R.string.video,
        )

        object OutdoorTasks : Screen(
            route = "outdoortasks/{childStage}/{difficulty}/{category}",
            title = R.string.outdoor_tasks,
        )

        object QuizResult : Screen(
            route = "quizresult/{score}/{childId}/{category}/{difficulty}/{childStage}/{quizDoneReason}",
            title = R.string.quiz_result,
        )
        object Leaderboard : Screen(
            route = "leaderboard/{childId}/{category}/{childStage}/{difficulty}",
            title = R.string.leaderboard,
        )*/
}





package ngui_maryanne.dissertation.publicparticipationplatform.utils

import android.content.Context
import ngui_maryanne.dissertation.publicparticipationplatform.R

object Constants {

    fun getSectors(context: Context): List<Pair<String, String>> {
        return listOf(
            context.getString(R.string.agriculture) to "Agriculture",
            context.getString(R.string.education) to "Education",
            context.getString(R.string.environment) to "Environment",
            context.getString(R.string.health) to "Health",
            context.getString(R.string.economic) to "Economic"
        )
    }


    val countiesMap = listOf(
        "All" to "All",
        "Mombasa" to "Mombasa",
        "Kwale" to "Kwale",
        "Kilifi" to "Kilifi",
        "Tana River" to "Tana River",
        "Lamu" to "Lamu",
        "Taita-Taveta" to "Taita-Taveta",
        "Garissa" to "Garissa",
        "Wajir" to "Wajir",
        "Mandera" to "Mandera",
        "Marsabit" to "Marsabit",
        "Isiolo" to "Isiolo",
        "Meru" to "Meru",
        "Tharaka-Nithi" to "Tharaka-Nithi",
        "Embu" to "Embu",
        "Kitui" to "Kitui",
        "Machakos" to "Machakos",
        "Makueni" to "Makueni",
        "Nyandarua" to "Nyandarua",
        "Nyeri" to "Nyeri",
        "Kirinyaga" to "Kirinyaga",
        "Murang'a" to "Murang'a",
        "Kiambu" to "Kiambu",
        "Turkana" to "Turkana",
        "West Pokot" to "West Pokot",
        "Samburu" to "Samburu",
        "Trans Nzoia" to "Trans Nzoia",
        "Uasin Gishu" to "Uasin Gishu",
        "Elgeyo-Marakwet" to "Elgeyo-Marakwet",
        "Nandi" to "Nandi",
        "Baringo" to "Baringo",
        "Laikipia" to "Laikipia",
        "Nakuru" to "Nakuru",
        "Narok" to "Narok",
        "Kajiado" to "Kajiado",
        "Kericho" to "Kericho",
        "Bomet" to "Bomet",
        "Kakamega" to "Kakamega",
        "Vihiga" to "Vihiga",
        "Bungoma" to "Bungoma",
        "Busia" to "Busia",
        "Siaya" to "Siaya",
        "Kisumu" to "Kisumu",
        "Homa Bay" to "Homa Bay",
        "Migori" to "Migori",
        "Kisii" to "Kisii",
        "Nyamira" to "Nyamira",
        "Nairobi City" to "Nairobi City"
    )

    val permissions = listOf(
        "create_policy",
        "create_poll",
        "create_projects",
        "update_policy_stage",
        "add_citizens",
        "approve_citizens"
    )
    const val OFFICIALS_REF = "Officials"
    const val NATIONAL_DB_REF = "National Database"
    const val AUDIT_LOGS_REF = "Audit Logs"
    const val REGISTERED_CITIZENS_REF = "Registered Citizens"
    const val POLICIES_REF = "Policies"
    const val POLLS_REF = "Polls"
    const val COMMENTS_REF = "Public Comments"
    const val PETITIONS_REF = "Petitions"
    const val BUDGETS_REF = "Budgets"
    const val NOTIFICATIONS_REF = "Notifications"
    const val ANNOUNCEMENTS_REF = "Announcements"
}
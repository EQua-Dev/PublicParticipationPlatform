package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp



import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.R
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.Locale


enum class AppLanguage(val code: String, val locale: Locale) {
    ENGLISH("en", Locale("en")),
    SWAHILI("sw", Locale("sw")),
    // Add more as needed

    ;

    companion object {
        fun fromCode(code: String): AppLanguage = values().find { it.code == code } ?: ENGLISH
    }
}

// Utility class to manage language settings
/*
object LanguageHelper {
    // SharedPreferences key
    private const val PREF_NAME = "app_language_pref"
    private const val KEY_LANGUAGE = "selected_language"

    // Save selected language
    fun saveLanguage(context: Context, language: AppLanguage) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
    fun getLanguage(context: Context): AppLanguage {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
        return AppLanguage.fromCode(languageCode ?: AppLanguage.ENGLISH.code)
    }

    // Update locale configuration
    fun updateLocale(context: Context, language: AppLanguage): ContextWrapper {
        var newContext = context
        val locale = language.locale
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            newContext = context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        return ContextWrapper(newContext)
    }

    // Restart the activity to apply language changes
    fun restartActivity(activity: Activity?) {
        activity?.let {
            val intent = it.intent
            it.finish()
            it.startActivity(intent)
            it.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
*/

object LanguageHelper {

    // Get saved language from DataStore
    suspend fun getLanguage(userPreferences: UserPreferences): AppLanguage {
        return userPreferences.languageFlow.first() // suspending call
    }

    // Save selected language to DataStore
    suspend fun saveLanguage(userPreferences: UserPreferences, language: AppLanguage) {
        userPreferences.saveLanguage(language)
    }

    // Update locale configuration
    fun updateLocale(context: Context, language: AppLanguage): ContextWrapper {
        val locale = language.locale
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            return ContextWrapper(context.createConfigurationContext(configuration))
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return ContextWrapper(context)
        }
    }

    // Restart the activity to apply language changes
    fun restartActivity(activity: Activity?) {
        activity?.let {
            val intent = it.intent
            it.finish()
            it.startActivity(intent)
            it.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}


@Composable
fun LanguageSettingSection(
    isEditing: Boolean,
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    userPreferences: UserPreferences
) {
    val languages = AppLanguage.values().toList()
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun findActivity(context: Context): Activity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is Activity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.preferred_language),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { expanded = true }
                    .padding(12.dp)
            ) {
                Text(
                    text = selectedLanguage.name.capitalize(),
                    style = MaterialTheme.typography.bodyMedium
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(text = language.name.capitalize()) },
                            onClick = {
                                expanded = false

                                coroutineScope.launch {
                                    // Save in datastore
                                    LanguageHelper.saveLanguage(userPreferences, language)

                                    // Update UI state
                                    onLanguageSelected(language)

                                    // Get the current language and compare before updating locale
                                    val currentLanguage = LanguageHelper.getLanguage(userPreferences)
                                    if (currentLanguage != language) {
                                        // Only update locale if the language is different
                                        val activity = findActivity(context)
                                        LanguageHelper.updateLocale(context, language)
                                        LanguageHelper.restartActivity(activity)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        } else {
            Text(
                text = selectedLanguage.name.capitalize().ifEmpty { "Not Set" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            )
        }
    }
}

// Extension function to capitalize first letter of enum name for display
private fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

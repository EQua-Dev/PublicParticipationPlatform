package ngui_maryanne.dissertation.publicparticipationplatform

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import ngui_maryanne.dissertation.publicparticipationplatform.holder.HolderScreen
import ngui_maryanne.dissertation.publicparticipationplatform.ui.theme.PublicParticipationPlatformTheme
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LocalScreenSize
import ngui_maryanne.dissertation.publicparticipationplatform.utils.getScreenSize
import ngui_maryanne.dissertation.publicparticipationplatform.providers.LocalNavHost
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage

import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.getSavedLanguagePreference
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.updateAppLanguage
import java.util.Locale

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun attachBaseContext(newBase: Context) {
        // Get saved language preference
        val sharedPreferences = newBase.getSharedPreferences("app_language_pref", Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString("selected_language", "en") ?: "en"

        val currentLanguage = Locale.getDefault().language
        if (currentLanguage != languageCode) {
            // Create a context with the correct locale if the language has changed
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val configuration = Configuration(newBase.resources.configuration)
            configuration.setLocale(locale)

            val context = newBase.createConfigurationContext(configuration)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
//        setAppLanguage(this)
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

// Request permissions
        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        enableEdgeToEdge()
        setContent {
            val defaultStatusBarColor = MaterialTheme.colorScheme.background.toArgb()
            var statusBarColor by remember { mutableStateOf(defaultStatusBarColor) }
            window.statusBarColor = statusBarColor

            /** Our navigation controller */
            val navController = rememberNavController()

            val context = LocalContext.current
            /** Getting screen size */
            val size = context.getScreenSize()
//            val currentLanguageCode: String = languageChangeHelper.getLanguageCode(applicationContext)

//            val localizedContext = remember(currentLocale) {
//                LanguageManager.setLocale(this@MainActivity, currentLocale)
//            }
            PublicParticipationPlatformTheme {
//                val localizedResources = rememberLocalizedResources(currentLocale)
                val selectedLocale = Locale("sw") // Or dynamically get this from a state or user setting
                val localizedContext = remember(selectedLocale) {
                    context.wrapInLocale(selectedLocale)
                }
                    CompositionLocalProvider(
                    LocalScreenSize provides size,
                    LocalNavHost provides navController,
//                        LocalContext provides localizedContext,

//                        LocalResources provides localizedResources


                    ) {
                        AppBackground{
                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
//                                color = MaterialTheme.colorScheme.background
                            ) {
                                HolderScreen(
                                    onLanguageChange = { selectedLang ->
//                                        currentLocale = selectedLang
                                    },
                                    onStatusBarColorChange = {
                                        //** Updating the color of the status bar *//*
                                        //** Updating the color of the status bar *//*
                                        //** Updating the color of the status bar *//*

                                        //** Updating the color of the status bar *//*
                                        statusBarColor = it.toArgb()
                                    }
                                )
                            }
                        }

                }


            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    }

    override fun onResume() {
        super.onResume()
        // Re-apply locale if needed
//        applyLocale()
    }

    private fun applyLocale() {
        try {
            val sharedPreferences = getSharedPreferences("app_language_pref", Context.MODE_PRIVATE)
            val languageCode = sharedPreferences.getString("selected_language", "en") ?: "en"

            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val resources = resources
            val configuration = Configuration(resources.configuration)
            configuration.setLocale(locale)

            // Update configuration
            resources.updateConfiguration(configuration, resources.displayMetrics)
        } catch (e: Exception) {
//            Log.e(TAG, "Error applying locale", e)
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Fine location permission granted
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Coarse location permission granted
            }

            else -> {
                // Location permissions denied
            }
        }
    }
    private fun setAppLanguage(context: Context) {
        val language = getSavedLanguagePreference(context)
        updateAppLanguage(context, language)
    }

}

@Composable
fun rememberLocalizedResources(languageCode: String): Resources {
    val context = LocalContext.current
    val configuration = remember { Configuration(context.resources.configuration) }

    return remember(languageCode) {
        configuration.setLocale(Locale(languageCode))
        context.createConfigurationContext(configuration).resources
    }
}

fun Context.wrapInLocale(locale: Locale): Context {
    val config = resources.configuration
    val newConfig = Configuration(config)
    newConfig.setLocale(locale)
    return createConfigurationContext(newConfig)
}
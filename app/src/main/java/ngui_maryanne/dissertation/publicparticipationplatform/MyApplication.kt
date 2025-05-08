
package ngui_maryanne.dissertation.publicparticipationplatform

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import dagger.hilt.android.HiltAndroidApp
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.LanguageHelper
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe.saveLanguagePreference

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun attachBaseContext(base: Context) {
        // Get saved language and apply it when app starts
        val language = LanguageHelper.getLanguage(base)
        super.attachBaseContext(LanguageHelper.updateLocale(base, language).baseContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Re-apply language configuration when system configuration changes
        val language = LanguageHelper.getLanguage(this)
        LanguageHelper.updateLocale(this, language)
    }
}



// Then modify your update function:
fun updateAppLanguage(context: Context, language: AppLanguage) {
    val langCode = when (language) {
        AppLanguage.ENGLISH -> "en"
        AppLanguage.SWAHILI -> "sw"
    }

    // Save preference
    saveLanguagePreference(context, language)

    // Update language
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
}
package ngui_maryanne.dissertation.publicparticipationplatform.utils

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.AppLanguage
import java.util.Locale

class LanguageManager {

    fun setLocale(context: Context, languageCode: String): Context {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getLanguageCode(context: Context,): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                ?.applicationLocales
                ?.get(0)?.toLanguageTag()?.split("-")?.first() ?: "en"
        } else {
            AppCompatDelegate.getApplicationLocales().get(0)?.toLanguageTag()?.split("-")?.first() ?: "en"
        }
//        return locale?.language ?: getDefaultLanguageCode()
    }

    private fun getDefaultLanguageCode(): String {
        return  AppLanguage.ENGLISH.code
    }
}

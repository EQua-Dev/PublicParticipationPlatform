package ngui_maryanne.dissertation.publicparticipationplatform.di

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resumeWithException

@Singleton
class TranslatorProvider @Inject constructor() {

    private val translatorMap = mutableMapOf<Pair<String, String>, Translator>()

    suspend fun getTranslator(sourceLang: String, targetLang: String): Translator {
        val key = sourceLang to targetLang

        return translatorMap.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build()

            val translator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            suspendCancellableCoroutine<Unit> { cont ->
                translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener { cont.resume(Unit) {} }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }

            translator
        }
    }
}

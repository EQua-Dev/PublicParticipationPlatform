package ngui_maryanne.dissertation.publicparticipationplatform.di

import android.content.Context
import android.location.Geocoder
//import awesomenessstudios.schoolprojects.publicparticipationplatform.BuildConfig
import ngui_maryanne.dissertation.publicparticipationplatform.utils.LocationUtils
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  /*  @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }*/

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideLocationUtils(@ApplicationContext context: Context): LocationUtils {
        return LocationUtils(context)
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault()) // Initialize Geocoder with the application context
    }

 /*   @Provides
    @Singleton
    fun provideOpenAIService(): OpenAIService {
        val apiKey = BuildConfig.OPEN_AI_KEY // Replace with your OpenAI API key
        return OpenAIService(apiKey)
    }*/
}
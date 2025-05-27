package ngui_maryanne.dissertation.publicparticipationplatform.holder


import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.CitizenProfileState
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import java.util.Locale
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class HolderViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val app: Application,
    private val auth: FirebaseAuth,
    private val citizenRepository: CitizenRepository,
    private val officialRepository: OfficialsRepository,
) : ViewModel() {

    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                }
        }
    }

    fun saveLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferences.saveLanguage(language)
        }
    }

    fun saveRole(role: UserRole) {
        viewModelScope.launch {
            userPreferences.saveRole(role)
        }
    }

    fun setLocale(language: AppLanguage) {
        val locale = when (language) {
            AppLanguage.ENGLISH -> Locale("en")
            AppLanguage.SWAHILI -> Locale("sw")
            else -> Locale("en")
        }

        Locale.setDefault(locale)
        val config = app.resources.configuration
        config.setLocale(locale)
        app.createConfigurationContext(config)
    }

    fun getUserType(
        userRole: String,
        onLoginSuccess: (UserRole) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val currentUserId = auth.currentUser?.uid ?: return onError(Exception("User not logged in"))

        when (userRole) {
            UserRole.CITIZEN.name -> {
                viewModelScope.launch {
                    val result = citizenRepository.getCitizen(currentUserId)
                    result.onSuccess {
                        onLoginSuccess(UserRole.CITIZEN)
                    }.onFailure {
                        onError(it)
                    }
                }
            }

            UserRole.OFFICIAL.name -> {
                viewModelScope.launch {
                    try {
                        officialRepository.getCurrentOfficial()
                        onLoginSuccess(UserRole.OFFICIAL)
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            }
            UserRole.SUPERADMIN.name -> {
                viewModelScope.launch {
                    val citizenResult = citizenRepository.getCitizen(currentUserId)
                    val isCitizen = citizenResult.isSuccess

                    val isOfficial = try {
                        officialRepository.getCurrentOfficial()
                        true
                    } catch (e: Exception) {
                        false
                    }

                    if (isCitizen || isOfficial) {
                        onError(Exception("User is not a valid super admin"))
                    } else {
                        onLoginSuccess(UserRole.SUPERADMIN)
                    }
                }
            }

            UserRole.UNKNOWN.name -> {
                onError(Exception("Unknown user role"))
            }
        }
    }


}

package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class OfficialsHomeViewModel @Inject constructor(
    private val officialsRepository: OfficialsRepository,
    private val notificationRepository: NotificationRepository,
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(OfficialsHomeState())
    val state: StateFlow<OfficialsHomeState> = _state.asStateFlow()


    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage


    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    loadOfficialData()
                    loadOfficialNotifications(lang)
                }
        }
    }

    private fun loadOfficialNotifications(lang: AppLanguage) {
        val userId = auth.currentUser?.uid ?: return

        notificationRepository.getUserNotificationsRealtime(
            userId = userId,
            language = lang,
            onResult = { notifications ->
                _state.value = _state.value.copy(
                    notifications = notifications.toMutableList()
                )
            },
            onError = { e ->
                Log.e("TAG", "Error fetching notifications: ${e.localizedMessage}")
            }
        )
    }
    private fun loadOfficialData() {
        viewModelScope.launch {
            officialsRepository.getOfficialByIdRealtime(officialId = auth.currentUser!!.uid).collect{ official ->
                Log.d("TAG", "loadCitizenData: ${official}")
                _state.value = _state.value.copy(
                    official = official,
//                    isApproved = citizen?.approved == "true"
                )
            }
        }
    }

    fun onEvent(event: OfficialsHomeEvent) {
        when (event) {
            is OfficialsHomeEvent.NavigateToProfile -> {
                // Handle navigation (passed to composable)
            }

            is OfficialsHomeEvent.Logout -> {
                // Handle logout
                viewModelScope.launch { officialsRepository.logout()
                    _state.update { _state.value.copy(logout = true) } }

            }

            OfficialsHomeEvent.LoadCitizenData -> loadOfficialData()
        }
    }
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class CitizenHomeViewModel @Inject constructor(
    private val citizenRepository: CitizenRepository,
    private val notificationRepository: NotificationRepository,
    private val announcementRepository: AnnouncementRepository,
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(CitizenHomeState())
    val state: StateFlow<CitizenHomeState> = _state.asStateFlow()

    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    private var announcementsListener: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    loadCitizenData()
                    loadCitizenNotifications(lang)
                    startListeningForAnnouncements(lang)
                }
        }
    }

/*    init {
        loadCitizenData()
        loadCitizenNotifications(lang)
        startListeningForAnnouncements(lang)
    }*/

    private fun loadCitizenData() {
        viewModelScope.launch {
            citizenRepository.getCitizenRealtime(citizenId = auth.currentUser!!.uid) { citizen ->
                Log.d("TAG", "loadCitizenData: ${citizen}")
                _state.value = _state.value.copy(
                    citizen = citizen,
                    isApproved = citizen?.approved == "true"
                )
            }
        }
    }

    private fun loadCitizenNotifications(lang: AppLanguage) {
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
    private fun startListeningForAnnouncements(lang: AppLanguage) {

        announcementRepository.getAllAnnouncementsRealtime(
            language = lang, // Add language parameter
            onResult = { announcements ->
                _state.value = _state.value.copy(
                    announcements = announcements.toMutableList(),
                    isLoading = false
                )
            },
            onError = { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Error loading announcements"
                )
                Log.e("AnnouncementViewModel", "Error fetching announcements: ${e.localizedMessage}")
            }
        )
    }

    fun onEvent(event: CitizenHomeEvent) {
        when (event) {
            is CitizenHomeEvent.NavigateToProfile -> {
                // Handle navigation (passed to composable)
            }

            is CitizenHomeEvent.Logout -> {
                // Handle logout
                viewModelScope.launch {
                    citizenRepository.logout()
                    _state.update { _state.value.copy(logout = true) }
                }

            }

            CitizenHomeEvent.LoadCitizenData -> loadCitizenData()
        }
    }

    override fun onCleared() {
        super.onCleared()
        announcementsListener?.remove()
    }
}
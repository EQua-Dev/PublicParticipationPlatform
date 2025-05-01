package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import javax.inject.Inject

@HiltViewModel
class CitizenHomeViewModel @Inject constructor(
    private val citizenRepository: CitizenRepository,
    private val notificationRepository: NotificationRepository,
    private val announcementRepository: AnnouncementRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(CitizenHomeState())
    val state: StateFlow<CitizenHomeState> = _state.asStateFlow()

    init {
        loadCitizenData()
        loadCitizenNotifications()
        startListeningForAnnouncements()
    }

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

    private fun loadCitizenNotifications() {
        val userId = auth.currentUser?.uid ?: return

        notificationRepository.getUserNotificationsRealtime(
            userId = userId,
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
    private fun startListeningForAnnouncements() {
        announcementRepository.getAllAnnouncementsRealtime(
            onResult = { announcements ->
                _state.value = _state.value.copy(
                    announcements = announcements.toMutableList(),
                )
            },
            onError = { e ->
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
}
package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.notification.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> = _notifications

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningForNotifications() {
        val userId = auth.currentUser?.uid ?: return

        listenerRegistration?.remove() // remove old listener

        listenerRegistration = notificationRepository.getUserNotificationsRealtime(
            userId = userId,
            onResult = { list ->
                _notifications.clear()
                _notifications.addAll(list)
            },
            onError = { e ->
                _error.value = e.message
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

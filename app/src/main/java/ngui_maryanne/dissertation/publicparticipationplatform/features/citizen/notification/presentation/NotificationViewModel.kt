package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.notification.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.AppNotification
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> = _notifications

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _loading = mutableStateOf<Boolean>(false)
    val loading: State<Boolean> = _loading


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


    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningForNotifications() {
        val userId = auth.currentUser?.uid ?: return

        listenerRegistration?.remove() // remove old listener
        _loading.value = true
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    listenerRegistration = notificationRepository.getUserNotificationsRealtime(
                        userId = userId,
                        language = lang,
                        onResult = { list ->
                            _loading.value = false
                            _notifications.clear()
                            _notifications.addAll(list)

                        },
                        onError = { e ->
                            _error.value = e.message
                        }
                    )
                }
        }

    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

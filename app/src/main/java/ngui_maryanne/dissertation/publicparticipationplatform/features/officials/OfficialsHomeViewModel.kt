package ngui_maryanne.dissertation.publicparticipationplatform.features.officials

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
import javax.inject.Inject

@HiltViewModel
class OfficialsHomeViewModel @Inject constructor(
    private val citizenRepository: CitizenRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(OfficialsHomeState())
    val state: StateFlow<OfficialsHomeState> = _state.asStateFlow()

    init {
        loadCitizenData()
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

    fun onEvent(event: OfficialsHomeEvent) {
        when (event) {
            is OfficialsHomeEvent.NavigateToProfile -> {
                // Handle navigation (passed to composable)
            }

            is OfficialsHomeEvent.Logout -> {
                // Handle logout
                viewModelScope.launch { citizenRepository.logout()
                    _state.update { _state.value.copy(logout = true) } }

            }

            OfficialsHomeEvent.LoadCitizenData -> loadCitizenData()
        }
    }
}
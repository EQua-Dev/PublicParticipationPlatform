package awesomenessstudios.schoolprojects.publicparticipationplatform.features.citizen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.citizenrepo.CitizenRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitizenHomeViewModel @Inject constructor(
    private val citizenRepository: CitizenRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(CitizenHomeState())
    val state: StateFlow<CitizenHomeState> = _state.asStateFlow()

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

    fun onEvent(event: CitizenHomeEvent) {
        when (event) {
            is CitizenHomeEvent.NavigateToProfile -> {
                // Handle navigation (passed to composable)
            }
            is CitizenHomeEvent.Logout -> {
                // Handle logout
            }

            CitizenHomeEvent.LoadCitizenData -> loadCitizenData()
        }
    }
}
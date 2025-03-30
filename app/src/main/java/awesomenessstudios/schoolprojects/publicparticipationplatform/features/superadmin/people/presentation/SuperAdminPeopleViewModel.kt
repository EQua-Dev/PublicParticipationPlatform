package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SuperAdminPeopleViewModel @Inject constructor() : ViewModel() {

    // UI State
    private val _state = mutableStateOf(SuperAdminUiState())
    val uiState: State<SuperAdminUiState> = _state


    // Handle user events
    fun onEvent(event: SuperAdminUiEvent) {
        when (event) {
            is SuperAdminUiEvent.SelectTab -> {
                _state.value = _state.value.copy(selectedTab = event.index)
            }
            is SuperAdminUiEvent.ToggleFabMenu -> {
                _state.value =_state.value.copy(isFabMenuExpanded = event.isExpanded)
            }
        }
    }
}
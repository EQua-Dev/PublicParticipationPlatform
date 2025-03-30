package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Official
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.OfficialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OfficialListViewModel @Inject constructor(
    private val repository: OfficialsRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<List<Official>>(emptyList())
    val uiState: State<List<Official>> = _uiState

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        fetchOfficials()
    }

    private fun fetchOfficials() {
        repository.getOfficialsRealtime { officials, error ->
            if (error != null) {
                _errorMessage.value = error
            } else {
                _uiState.value = officials
            }
        }
    }
}

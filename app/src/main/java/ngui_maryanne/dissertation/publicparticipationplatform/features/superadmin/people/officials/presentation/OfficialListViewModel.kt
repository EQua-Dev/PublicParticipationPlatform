package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.officialsrepo.OfficialsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OfficialListState(
    val officials: List<Official> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OfficialListViewModel @Inject constructor(
    private val repository: OfficialsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(OfficialListState())
    val uiState: StateFlow<OfficialListState> = _uiState.asStateFlow()

    init {
        loadOfficials()
    }

    fun loadOfficials() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.getOfficialsRealtime { officials, s ->
                    _uiState.value = _uiState.value.copy(
                        officials = officials,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to load officials: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
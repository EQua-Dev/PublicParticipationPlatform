package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.time.LocalDateTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CitizenPetitionsViewModel @Inject constructor(
    private val repository: PetitionRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitizenPetitionsUiState())
    val uiState: StateFlow<CitizenPetitionsUiState> = _uiState.asStateFlow()

    private val _events = Channel<PetitionEvent>()
    val events = _events.receiveAsFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                _uiState.update { it.copy(currentUserRole = role ?: UserRole.CITIZEN) }
            }
        }
        observePetitions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: PetitionEvent) {
        when (event) {
            is PetitionEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                applyFilters()
            }
            is PetitionEvent.OnSectorFilterChanged -> {
                _uiState.update { it.copy(selectedSector = event.sector) }
                applyFilters()
            }
            PetitionEvent.OnToggleCreatePetition -> {
                _uiState.update { it.copy(isCreatingNewPetition = !it.isCreatingNewPetition) }
            }
            PetitionEvent.RefreshPetitions -> {
                _uiState.update { it.copy(isLoading = true) }
                observePetitions()
            }
            PetitionEvent.OnErrorShown -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun observePetitions() {
        listenerRegistration?.remove()

        listenerRegistration = repository.getAllPetitionsListener { petitions ->
            viewModelScope.launch {
                try {
                    val sectors = petitions.map { it.sector }.toSet()

                    _uiState.update {
                        it.copy(
                            allPetitions = petitions,
                            availableSectors = sectors,
                            isLoading = false,
                            error = null,
                            lastUpdated = LocalDateTime.now()
                        )
                    }
                    applyFilters()
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load petitions"
                        )
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        _uiState.update { currentState ->
            val filtered = applyFilters(
                currentState.allPetitions,
                currentState.searchQuery,
                currentState.selectedSector
            )

            currentState.copy(
                filteredPetitions = filtered,
                petitionsBySector = groupBySector(filtered)
            )
        }
    }

    private fun applyFilters(
        petitions: List<Petition>,
        query: String,
        sector: String?
    ): List<Petition> {
        return petitions.filter { petition ->
            val matchesSearch = query.isBlank() ||
                    petition.title.contains(query, ignoreCase = true) ||
                    petition.description.contains(query, ignoreCase = true)

            val matchesSector = sector == null || petition.sector == sector

            matchesSearch && matchesSector
        }
    }

    private fun groupBySector(petitions: List<Petition>): Map<String, List<Petition>> {
        return petitions.groupBy { it.sector }
    }

    override fun onCleared() {
        listenerRegistration?.remove()
        super.onCleared()
    }
}

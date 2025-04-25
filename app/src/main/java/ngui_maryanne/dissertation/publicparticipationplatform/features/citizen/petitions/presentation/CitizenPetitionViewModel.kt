package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.petitions.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.petitionrepo.PetitionRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import javax.inject.Inject

@HiltViewModel
class CitizenPetitionsViewModel @Inject constructor(
    private val repository: PetitionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = mutableStateOf(CitizenPetitionsUiState())
    val uiState: State<CitizenPetitionsUiState> = _uiState

    private var listener: ListenerRegistration? = null

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                if (role != null) {
                    _uiState.value = _uiState.value.copy(
                        currentUserRole = role.name
                    )
                }
            }
        }
    }

    init {
        observePetitions()
    }

    fun onEvent(event: PetitionEvent) {
        when (event) {
            is PetitionEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
                applySearch()
            }

            is PetitionEvent.OnToggleCreatePetition -> {
                _uiState.value = _uiState.value.copy(
                    isCreatingNewPetition = !_uiState.value.isCreatingNewPetition
                )
            }
        }
    }

    private fun observePetitions() {
        listener = repository.getAllPetitionsListener { petitions ->
            _uiState.value = _uiState.value.copy(
                allPetitions = petitions,
                petitionsBySector = groupBySector(
                    applySearchFilter(
                        petitions,
                        _uiState.value.searchQuery
                    )
                ),
                isLoading = false
            )
        }
    }

    private fun applySearch() {
        val filtered = applySearchFilter(_uiState.value.allPetitions, _uiState.value.searchQuery)
        _uiState.value = _uiState.value.copy(petitionsBySector = groupBySector(filtered))
    }

    private fun applySearchFilter(list: List<Petition>, query: String): List<Petition> {
        return if (query.isBlank()) list else list.filter {
            it.title.contains(query, true) || it.description.contains(query, true)
        }
    }

    private fun groupBySector(petitions: List<Petition>): Map<String, List<Petition>> {
        return petitions.groupBy { it.sector }
    }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }
}

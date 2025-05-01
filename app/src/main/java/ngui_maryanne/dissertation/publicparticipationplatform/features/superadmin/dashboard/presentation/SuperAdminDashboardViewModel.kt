package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.dashboard.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.admindashboardrepo.AdminDashboardRepository
import javax.inject.Inject

@HiltViewModel
class SuperAdminDashboardViewModel @Inject constructor(
    private val repository: AdminDashboardRepository
) : ViewModel() {


    private val _state = mutableStateOf(SuperAdminDashboardState())
    val state: State<SuperAdminDashboardState> = _state

    init {
        viewModelScope.launch {
            repository.getCitizenCount().collect { count ->
                _state.value = _state.value.copy(citizensCount = count)
            }
        }
        viewModelScope.launch {
            repository.getOfficialCount().collect { count ->
                _state.value = _state.value.copy(officialsCount = count)
            }
        }

        viewModelScope.launch {
            repository.getPoliciesCount().collect { count ->
                _state.value = _state.value.copy(policiesCount = count)
            }
        }

        viewModelScope.launch {
            repository.getPollsCount().collect { count ->
                _state.value = _state.value.copy(pollsCount = count)
            }
        }

        viewModelScope.launch {
            repository.getBudgetsCount().collect { count ->
                _state.value = _state.value.copy(budgetsCount = count)
            }
        }

        viewModelScope.launch {
            repository.getPetitionsCount().collect { count ->
                _state.value = _state.value.copy(petitionsCount = count)
            }
        }

        // Do the same for polls, budgets, petitions...
    }


    fun onEvent(event: SuperAdminDashboardEvent) {
        when (event) {
            is SuperAdminDashboardEvent.LoadDashboardData -> {
                loadDashboardData()
            }

            is SuperAdminDashboardEvent.CardClicked -> {
                // Handle card clicks if needed
            }
        }
    }

    private fun loadDashboardData() {
        /*viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val citizens = repository.getCitizenCount()
                val policies = repository.getPoliciesCount()
                val polls = repository.getPollsCount()
                val budgets = repository.getBudgetsCount()
                val petitions = repository.getPetitionsCount()

                _state.value = SuperAdminDashboardState(
                    citizensCount = citizens,
                    policiesCount = policies,
                    pollsCount = polls,
                    budgetsCount = budgets,
                    petitionsCount = petitions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }*/
    }
}

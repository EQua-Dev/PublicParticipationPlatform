package awesomenessstudios.schoolprojects.publicparticipationplatform.features.officials.citizens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.TransactionTypes
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.models.Citizen
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.BlockChainRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.CitizenRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.NationalDBRepository
import awesomenessstudios.schoolprojects.publicparticipationplatform.repositories.OfficialsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfficialCitizenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val citizenRepository: CitizenRepository,
    private val officialRepository: OfficialsRepository,
    private val nationalDBRepository: NationalDBRepository,
    private val blockChainRepository: BlockChainRepository,
) : ViewModel() {


    private val _state = mutableStateOf(CitizenState())
    val state: State<CitizenState> = _state

    init {
        onEvent(CitizenEvent.LoadData)
    }

    fun onEvent(event: CitizenEvent) {
        when (event) {
            CitizenEvent.LoadData -> loadData()
            is CitizenEvent.SelectCitizen -> selectCitizen(event.citizen)
            CitizenEvent.ApproveCitizen -> approveCitizen()
            CitizenEvent.RejectCitizen -> rejectCitizen()
            CitizenEvent.DismissBottomSheet -> dismissBottomSheet()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val citizens = citizenRepository.getAllCitizens()
                val official = officialRepository.getCurrentOfficial()
                _state.value = _state.value.copy(
                    citizens = citizens,
                    official = official,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    private fun selectCitizen(citizen: Citizen) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val nationalCitizen = nationalDBRepository.getCitizenByNationalId(citizen.nationalID)
                _state.value = _state.value.copy(
                    selectedCitizen = citizen,
                    nationalCitizen = nationalCitizen,
                    showBottomSheet = true,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    selectedCitizen = citizen,
                    nationalCitizen = null,
                    showBottomSheet = true,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun approveCitizen() {
        viewModelScope.launch {
            _state.value.selectedCitizen?.let { citizen ->
                _state.value = _state.value.copy(isLoading = true)
                try {
                    citizenRepository.approveCitizen(citizen.id)
                    // Call blockchain function
                    blockChainRepository.createBlockchainTransaction(auth.currentUser!!.uid, TransactionTypes.APPROVE_CITIZEN)
                    _state.value = _state.value.copy(
                        showBottomSheet = false,
                        isLoading = false
                    )
                    // Refresh data
                    onEvent(CitizenEvent.LoadData)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun rejectCitizen() {
        _state.value = _state.value.copy(showBottomSheet = false)
    }

    private fun dismissBottomSheet() {
        _state.value = _state.value.copy(showBottomSheet = false)
    }

}
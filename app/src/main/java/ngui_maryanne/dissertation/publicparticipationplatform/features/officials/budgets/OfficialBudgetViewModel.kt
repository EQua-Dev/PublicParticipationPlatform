package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.NotificationTypes
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.UserRole
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Announcement
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Budget
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.announcementrepo.AnnouncementRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OfficialBudgetViewModel @Inject constructor(
    private val budgetRepo: BudgetRepository,
    private val storageRepo: StorageRepository,
    private val announcementRepository: AnnouncementRepository,
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(OfficialBudgetUiState())
    val uiState: StateFlow<OfficialBudgetUiState> = _uiState.asStateFlow()


    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                    fetchBudgets(lang)
                }

        }
    }

    /*   init {
           fetchBudgets(lang)

       }*/

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

    fun onEvent(event: OfficialBudgetEvent) {
        when (event) {
            is OfficialBudgetEvent.OnAmountChanged -> {
                _uiState.value = _uiState.value.copy(amount = event.value)
            }

            is OfficialBudgetEvent.OnNoteChanged -> {
                _uiState.value = _uiState.value.copy(budgetNote = event.value)
            }

            is OfficialBudgetEvent.OnImpactChanged -> {
                _uiState.value = _uiState.value.copy(impact = event.value)
            }

            is OfficialBudgetEvent.OnAddBudgetOption -> {
                _uiState.value = _uiState.value.copy(
                    budgetOptions = _uiState.value.budgetOptions + BudgetOptionInput()
                )
            }

            is OfficialBudgetEvent.OnBudgetOptionChanged -> {
                val updated = _uiState.value.budgetOptions.toMutableList()
                updated[event.index] = event.option
                _uiState.value = _uiState.value.copy(budgetOptions = updated)
            }

            is OfficialBudgetEvent.SubmitBudget -> {
                submitBudget()
            }

            is OfficialBudgetEvent.OnFabClicked -> {
                _uiState.value = _uiState.value.copy(navigateToCreateBudget = true)
            }

            is OfficialBudgetEvent.OnNavigateDone -> {
                _uiState.value = _uiState.value.copy(navigateToCreateBudget = false)
            }

            is OfficialBudgetEvent.OnBudgetOptionImageSelected -> {
                val updatedOptions = _uiState.value.budgetOptions.toMutableList()
                updatedOptions[event.index] = updatedOptions[event.index].copy(imageUri = event.uri)
                _uiState.value = _uiState.value.copy(budgetOptions = updatedOptions)
            }

            OfficialBudgetEvent.OnResetCreateState -> TODO()
        }
    }

    private fun fetchBudgets(lang: AppLanguage) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            budgetRepo.getAllBudgets(lang).collect { budgetList ->
                val displayBudgetList =
                    if (_uiState.value.currentUserRole == UserRole.OFFICIAL.name) budgetList else budgetList.filter { it.isActive }
                _uiState.value = _uiState.value.copy(
                    budgets = displayBudgetList,
                    isLoading = false
                )
            }
        }
    }

    fun submitBudget() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val budgetId = UUID.randomUUID().toString()
                val hash = UUID.randomUUID().toString()

                val options = _uiState.value.budgetOptions.map { input ->
                    val imageUrl = input.imageUri?.let {
                        storageRepo.uploadImage("budget_options/${UUID.randomUUID()}.jpg", it)
                    } ?: ""

                    BudgetOption(
                        optionId = UUID.randomUUID().toString(),
                        optionProjectName = input.projectName,
                        optionDescription = input.description,
                        optionAssociatedPolicy = input.associatedPolicy,
                        optionAmount = input.amount,
                        imageUrl = imageUrl
                    )
                }
                val budget = Budget(
                    id = budgetId,
                    hash = hash,
                    budgetNo = "BGT-${System.currentTimeMillis()}",
                    amount = _uiState.value.amount,
                    budgetNote = _uiState.value.budgetNote,
                    budgetOptions = options,
                    responses = listOf(),
                    createdBy = auth.currentUser!!.uid, // Replace this with actual user ID
                    dateCreated = System.currentTimeMillis().toString(),
                    budgetExpiry = System.currentTimeMillis().plus(30 * 24 * 60 * 60 * 1000L)
                        .toString(),
                    impact = _uiState.value.impact,
                    isActive = true
                )
                budgetRepo.createBudget(budget)
                val announcement = Announcement(
                    id = UUID.randomUUID().toString(),
                    createdBy = budget.createdBy,
                    createdAt = System.currentTimeMillis().toString(),
                    type = NotificationTypes.BUDGET,
                    typeId = budgetId,
                    title = "New Budget",
                    description = "A new budget: ${budget.impact} has been created",
                )

                announcementRepository.addAnnouncement(announcement, announcement.type)
                _uiState.value = OfficialBudgetUiState() // Reset UI
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

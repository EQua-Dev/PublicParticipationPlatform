package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.budgets.budgetddetails

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetOption
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.BudgetResponse
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Petition
import ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.budgetrepo.BudgetRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.notificationrepo.NotificationRepository
import ngui_maryanne.dissertation.publicparticipationplatform.repositories.storagerepo.StorageRepository
import ngui_maryanne.dissertation.publicparticipationplatform.utils.HelpMe
import ngui_maryanne.dissertation.publicparticipationplatform.utils.UserPreferences
import java.util.Objects.hash
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OfficialBudgetDetailsViewModel @Inject constructor(
    private val repository: BudgetRepository,
    private val notificationRepository: NotificationRepository,
    private val storageRepository: StorageRepository,
    private val auth: FirebaseAuth,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetDetailsState())
    val uiState: StateFlow<BudgetDetailsState> = _uiState.asStateFlow()

    private val _selectedLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val selectedLanguage: State<AppLanguage> = _selectedLanguage

    init {
        viewModelScope.launch {
            userPreferences.languageFlow
                .distinctUntilChanged()
                .collect { lang ->
                    Log.d("TAG", "selected language: $lang")
                    _selectedLanguage.value = lang
                }
        }
    }


    fun onEvent(event: BudgetDetailsEvent) {
        when (event) {
            is BudgetDetailsEvent.LoadBudget -> {
                loadBudget(event.budgetId)
            }

            is BudgetDetailsEvent.VoteOption -> {
//                voteForOption(event.optionId)
            }

            is BudgetDetailsEvent.ToggleActivation -> {
                toggleActivation()
            }

            is BudgetDetailsEvent.OnErrorShown -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
        }
    }

    private fun loadBudget(budgetId: String) {
        viewModelScope.launch {
            repository.getBudgetById(budgetId, _selectedLanguage.value).collect { budget ->
                if (budget != null) {
                    val votedOption = budget.responses.find { it.userId == auth.currentUser!!.uid }
                    val votedOptionId = votedOption?.optionId

                    _uiState.value = _uiState.value.copy(
                        budget = budget,
                        budgetOptions = budget.budgetOptions,
                        votedOptionId = votedOptionId,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Budget not found",
                        isLoading = false
                    )
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyAndVoteOption(
        activity: FragmentActivity,
        optionId: String,
        hashType: String,
        optionName: String,
//        answer1: String,
//        answer2: String,
//        securityQuestion1: String,
//        securityQuestion2: String,
//        storedSecurityHash: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        /*       val hash1 = hash(securityQuestion1 + answer1, hashType)
               val hash2 = hash(securityQuestion2 + answer2, hashType)
               val combinedHash = hash(hash1 + hash2, hashType)

               if (combinedHash != storedSecurityHash) {
                   onFailure("Security answers don't match")
                   return
               }
       */
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Voting Budget For $optionName",
            onSuccess = {
                voteForOption(optionId, hashType, isAnonymous, onSuccess, onFailure)
            },
            onNoHardware = {
                voteForOption(optionId, hashType, isAnonymous, onSuccess, onFailure)
            }
        )
    }


    private fun voteForOption(
        optionId: String, hashType: String,
        isAnonymous: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            try {
                val signatureId = UUID.randomUUID().toString()
                val answerHash = hash(
                    auth.currentUser!!.uid + currentState.budget!!.id + optionId + System.currentTimeMillis(),
                    hashType
                )

                // Call the repository function to vote for a budget option
                val voteOption = BudgetResponse(
                    answerId = UUID.randomUUID().toString(),
                    answerHash = answerHash.toString(),
                    userId = auth.currentUser!!.uid,
                    optionId = optionId,
//                            isAnonymous =
                    dateCreated = System.currentTimeMillis().toString()
                )

                val updatedResponses = currentState.budget.responses.toMutableList().apply {
                    add(voteOption)
                }

                repository.voteForBudgetOption(currentState.budget!!.id, updatedResponses)
                notificationRepository.sendBudgetVoteNotifications(
                    currentState.budget,
                    auth.currentUser!!.uid
                )
                onSuccess()
                // Update the UI state to reflect the voted option
                _uiState.value = currentState.copy(votedOptionId = optionId)
            } catch (e: Exception) {
                onFailure(e.message ?: "Signing petition failed")
                // If an error occurs, update the UI state with the error message
                _uiState.value = currentState.copy(error = e.message ?: "Vote failed")
            }
        }
    }


    private fun toggleActivation() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                // Toggle the activation status of the budget
                repository.toggleBudgetActivation(
                    currentState.budget!!.id,
                    !currentState.budget!!.isActive
                )

                // Update the UI state to reflect the new activation status
//                _uiState.value = currentState.copy(isActive = !currentState.isActive)
            } catch (e: Exception) {
                // If there's an error, update the UI state with the error message
                _uiState.value =
                    _uiState.value.copy(error = e.message ?: "Could not update budget status")
            }
        }
    }

    fun submitBudgetEdit(
        budgetId: String,
        amount: String,
        note: String,
        impact: String,
        budgetOptions: MutableList<BudgetOption>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val originalBudget = _uiState.value.budget ?: return@launch

            try {
                val updateMap = mutableMapOf<String, Any>()

                // Check top-level field changes
                if (amount != originalBudget.amount) updateMap["amount"] = amount
                if (note != originalBudget.budgetNote) updateMap["budgetNote"] = note
                if (impact != originalBudget.impact) updateMap["impact"] = impact

                // Compare budgetOptions
                val updatedBudgetOptions = mutableListOf<BudgetOption>()
                var hasOptionsChanged = false

                budgetOptions.forEachIndexed { index, newOption ->
                    val oldOption = originalBudget.budgetOptions.getOrNull(index)

                    if (oldOption != null) {
                        var optionChanged = false
                        var imageUrl = oldOption.imageUrl

                        // Check if image URL is a new Uri that needs uploading
                        if (newOption.imageUrl != null &&
                            newOption.imageUrl != oldOption.imageUrl &&
                            !newOption.imageUrl!!.startsWith("https://")
                        ) {
                            imageUrl = storageRepository.uploadImage("budget_images/", newOption.imageUrl!!.toUri())
                            optionChanged = true
                        }

                        if (
                            newOption.optionProjectName != oldOption.optionProjectName ||
                            newOption.optionDescription != oldOption.optionDescription ||
                            newOption.optionAmount != oldOption.optionAmount ||
                            imageUrl != oldOption.imageUrl
                        ) {
                            optionChanged = true
                        }

                        if (optionChanged) {
                            hasOptionsChanged = true
                            updatedBudgetOptions.add(
                                newOption.copy(imageUrl = imageUrl)
                            )
                        } else {
                            updatedBudgetOptions.add(oldOption)
                        }
                    } else {
                        // New option added
                        hasOptionsChanged = true
                        var imageUrl = newOption.imageUrl
                        if (imageUrl != null && !imageUrl.startsWith("https://")) {
                            imageUrl = storageRepository.uploadImage("budget_images/", imageUrl.toUri())
                        }
                        updatedBudgetOptions.add(newOption.copy(imageUrl = imageUrl))
                    }
                }

                if (hasOptionsChanged) {
                    updateMap["budgetOptions"] = updatedBudgetOptions
                }

                if (updateMap.isNotEmpty()) {
                    repository.updateBudgetDetails(budgetId, updateMap)
                    notificationRepository.sendBudgetVoteNotifications(originalBudget, auth.currentUser!!.uid)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        editSuccess = true,
                        message = "Budget successfully updated"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        editSuccess = false,
                        message = "No changes to update"
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    editSuccess = false,
                    message = e.message ?: "An error occurred while updating the budget"
                )
            }
        }
    }


}

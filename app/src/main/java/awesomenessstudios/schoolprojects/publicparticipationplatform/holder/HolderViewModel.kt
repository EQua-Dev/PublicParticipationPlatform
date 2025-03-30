package awesomenessstudios.schoolprojects.publicparticipationplatform.holder


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.publicparticipationplatform.data.enums.UserRole
import awesomenessstudios.schoolprojects.publicparticipationplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A View model with hiltViewModel annotation that is used to access this view model everywhere needed
 */
@HiltViewModel
class HolderViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun saveRole(role: UserRole) {
        viewModelScope.launch {
            userPreferences.saveRole(role)
        }
    }

}
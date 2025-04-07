package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import android.net.Uri

data class AddCitizenUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val nationalId: String = "",
    val profileImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

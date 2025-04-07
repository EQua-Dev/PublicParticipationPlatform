package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.officials.presentation

import android.net.Uri

data class CreateOfficialUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val permissions: Set<String> = emptySet(),
    val profileImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)
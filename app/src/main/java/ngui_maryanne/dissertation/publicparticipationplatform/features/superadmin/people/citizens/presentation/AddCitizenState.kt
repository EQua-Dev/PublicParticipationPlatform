package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

data class AddCitizenUiState(
    val name: String = "",
    val phoneNumber: String = "",
    val nationalId: String = "",
    val profileImageUri: Uri? = null,
    val citizens: List<Citizen> = emptyList(),
    val selectedCitizen: Citizen? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val showBottomSheet: Boolean = false
)

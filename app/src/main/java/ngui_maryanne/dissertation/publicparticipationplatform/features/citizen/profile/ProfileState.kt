package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

data class CitizenProfileState(
    val citizen: Citizen? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val updateSuccess: Boolean = false,
    val error: String? = null,

    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val occupation: String = "",
    val countyOfResidence: String = "",

    val profileImageUri: Uri? = null,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH
)

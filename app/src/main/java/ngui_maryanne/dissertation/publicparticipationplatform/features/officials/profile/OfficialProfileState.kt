package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Official

data class OfficialProfileState(
    val official: Official = Official(),
    val editedPhoneNumber: String = "",
    val profileImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val error: String? = null,
    val updateSuccess: Boolean = false
)
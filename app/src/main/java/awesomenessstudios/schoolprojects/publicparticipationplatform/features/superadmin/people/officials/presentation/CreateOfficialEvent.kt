package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.officials.presentation

import android.net.Uri

sealed class CreateOfficialUiEvent {
    data class UpdateFirstName(val value: String) : CreateOfficialUiEvent()
    data class UpdateLastName(val value: String) : CreateOfficialUiEvent()
    data class UpdateEmail(val value: String) : CreateOfficialUiEvent()
    data class UpdatePhoneNumber(val value: String) : CreateOfficialUiEvent()
    data class TogglePermission(val permission: String) : CreateOfficialUiEvent()
    data class UpdateProfileImage(val uri: Uri) : CreateOfficialUiEvent()
    object CreateOfficial : CreateOfficialUiEvent()
}
package awesomenessstudios.schoolprojects.publicparticipationplatform.features.superadmin.people.citizens.presentation

import android.net.Uri

sealed class AddCitizenEvent {
    data class EnteredName(val value: String) : AddCitizenEvent()
    data class EnteredPhone(val value: String) : AddCitizenEvent()
    data class EnteredNationalId(val value: String) : AddCitizenEvent()
    data class SelectedProfileImage(val uri: Uri?) : AddCitizenEvent()
    object Submit : AddCitizenEvent()
}

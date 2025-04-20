package ngui_maryanne.dissertation.publicparticipationplatform.features.superadmin.people.citizens.presentation

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen
import ngui_maryanne.dissertation.publicparticipationplatform.features.officials.citizens.CitizenEvent

sealed class AddCitizenEvent {
    data class EnteredName(val value: String) : AddCitizenEvent()
    data class EnteredPhone(val value: String) : AddCitizenEvent()
    data class EnteredNationalId(val value: String) : AddCitizenEvent()
    data class SelectedProfileImage(val uri: Uri?) : AddCitizenEvent()
    data class SelectCitizen(val citizen: Citizen) : AddCitizenEvent()
    object LoadData: AddCitizenEvent()
    object Submit : AddCitizenEvent()
    object DismissBottomSheet : AddCitizenEvent()
}

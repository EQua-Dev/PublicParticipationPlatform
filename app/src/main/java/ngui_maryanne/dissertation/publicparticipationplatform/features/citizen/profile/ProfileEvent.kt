package ngui_maryanne.dissertation.publicparticipationplatform.features.citizen.profile

import android.net.Uri
import ngui_maryanne.dissertation.publicparticipationplatform.data.enums.AppLanguage
import ngui_maryanne.dissertation.publicparticipationplatform.data.models.Citizen

sealed class CitizenProfileEvent {
    object LoadProfile : CitizenProfileEvent()
    object ToggleEditMode : CitizenProfileEvent()
    object SaveProfile : CitizenProfileEvent()
    object DismissSuccess : CitizenProfileEvent()

    data class FirstNameChanged(val value: String) : CitizenProfileEvent()
    data class LastNameChanged(val value: String) : CitizenProfileEvent()
    data class PhoneNumberChanged(val value: String) : CitizenProfileEvent()
    data class OccupationChanged(val value: String) : CitizenProfileEvent()
    data class CountyOfResidenceChanged(val value: String) : CitizenProfileEvent()
    data class ProfileImageSelected(val uri: Uri) : CitizenProfileEvent()
    data class LanguageChanged(val language: AppLanguage) : CitizenProfileEvent()
}

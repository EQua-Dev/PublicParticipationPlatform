package ngui_maryanne.dissertation.publicparticipationplatform.features.officials.profile

import android.net.Uri

sealed class OfficialProfileEvent {
    object LoadProfile : OfficialProfileEvent()
    object ToggleEditMode : OfficialProfileEvent()
    data class PhoneNumberChanged(val newNumber: String) : OfficialProfileEvent()
    data class ProfileImageSelected(val uri: Uri) : OfficialProfileEvent()
    object SaveProfile : OfficialProfileEvent()
    object DismissSuccess : OfficialProfileEvent()
}
package ngui_maryanne.dissertation.publicparticipationplatform.data.models

data class Official(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val permissions: List<String> = emptyList(),
    val profileImageUrl: String? = null,
    val initialPassword: String? = "",
    val active: Boolean = true
)

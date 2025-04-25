package ngui_maryanne.dissertation.publicparticipationplatform.data.enums

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    SWAHILI("sw");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

package tech.merajobs.dataModel

data class LanguageDataModel(
    val data: ArrayList<LanguageData>? = null,
    val message: List<String>,
    val success: Boolean,
)

data class LanguageData(
    val id : String,
    val job_seeker_id : String,
    val language_id : String,
    val proficiency : String,
    val read : String,
    val write : String,
    val speak : String,
    val language : String,
    val proficiency_level : String,
)
package tech.merajobs.dataModel

data class PatentDataModel(
    val success: String,
    val data: ArrayList<PatentList>? = null,
)

data class PatentList(
    val id : String,
    val title : String,
    val url : String,
    val description : String,
    val application_no : String,
    val office : String,
    val job_seeker_id : String,
    val issued_date : String,
)

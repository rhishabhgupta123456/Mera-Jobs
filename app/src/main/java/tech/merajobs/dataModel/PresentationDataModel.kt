package tech.merajobs.dataModel

data class PresentationDataModel(
    val success: String,
    val data: ArrayList<PresentationList>? = null,
)

data class PresentationList(
    val id : String,
    val job_seeker_id : String,
    val title : String,
    val url : String,
    val description : String,
)

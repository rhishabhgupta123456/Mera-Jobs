package tech.merajobs.dataModel

data class WhitePaperDataModel(
    val success: String,
    val data: ArrayList<WhitePaperList>? = null,
)

data class WhitePaperList(
    val id : String,
    val title : String,
    val url : String,
    val description : String,
    val published_date : String,
    val job_seeker_id : String,
)

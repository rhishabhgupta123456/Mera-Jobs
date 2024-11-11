package tech.merajobs.dataModel

data class WorkSampleDataModel(
    val success: String,
    val data: ArrayList<WorkSampleList>? = null,
)

data class WorkSampleList(
    val id : String,
    val title : String,
    val url : String,
    val description : String,
    val duration_year : String,
    val duration_months : String,
    val job_seeker_id : String,
)

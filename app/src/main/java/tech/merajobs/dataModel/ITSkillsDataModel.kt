package tech.merajobs.dataModel

data class ITSkillsDataModel(
    val success : Boolean,
    val data : ArrayList<ITSkillsData>? = null
)

data class ITSkillsData(
    val id : String,
    val experience_years : String,
    val experience_months : String,
    val job_seeker_id : String,
    val last_used_year : String,
    val skill_id : String,
    val version : String,
    val skill_name : String,

)

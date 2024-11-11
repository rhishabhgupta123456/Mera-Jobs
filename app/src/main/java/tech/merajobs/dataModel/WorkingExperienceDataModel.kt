package tech.merajobs.dataModel

data  class WorkingExperienceDataModel(
    val success : Boolean,
    val data : ArrayList<WorkExperienceData>? = null

)

data class WorkExperienceData (
    val id : String,
    val job_seeker_id : String,
    val currently_working : String,
    val employment_type : String,
    val total_exp_years : String,
    val total_exp_months : String,
    val company_id : String,
    val job_title : String,
    val joning_date : String,
    val exit_date : String?,
    val job_profile : String?,
    val skills : ArrayList<Int>?,
    val skills_label : ArrayList<String>?,
    var company_logo : String? = null,
    var company : Company? = null,
    var notice_period : String?,
    var employment_type_label : String?,
    var notice_period_label : String?,
)

data class Company(
    val company_name : String?
)
package tech.merajobs.dataModel

data class JobDataModel(
    val data: ArrayList<JobList>?,
    val message: List<String>,
    val pagination: Pagination,
    val success: Boolean,
)

data class Pagination(
    val current_page: Int,
    val offset: Int,
    val page_size: Int,
    val total: Int,
    val total_pages: Int,
)

data class JobList(
    val active: String,
    val address: String,
    val city_name: String,
    val company_logo: String?,
    val company_name: String? = "DEMO NAME",
    val country_name: String,
    val course_id: Int,
    val course: String? = null,
    val currency: String,
    val department_id: Int,
    val department_name: String,
    val description: String,
    val employer_id: String,
    val employment_type: Int,
    val employment_type_label: String? = null,
    val experience: String,
    val experience_label: String? = null,
    val good_to_have: String,
    val id: Int,
    val industry_id: Int,
    val industry_name: String,
    val is_applied: Int,
    val is_negotiable: String,
    var is_saved: Int,
    var saved_job_id: Int,
    val job_label: String,
    val job_label_display: String,
    val last_apply_date: String,
    val location: Int,
    val notice_period: String,
    val position: Int,
    val posted_date: String,
    val qualification: String,
    val qualification_id: Int,
    val role: String,
    val role_id: Int,
    val salary_label: String? = null,
    val salary_range: Int,
    val salary_type: String,
    val salary_type_label: String? = null,
    val skills: String,
    val slug: String,
    val specialization: String?,
    val specialization_id: String,
    val state_name: String,
    val title: String,
    val view_count: String,
    val work_location_type: String,
    val work_location_type_label: String? = null,
    val zip: String,
    val employer: Employer,
)

data class Employer(
    var id: String,
    var photo: String?,
    var name: String,
    var mobile: String,
    var whatsapp_notification_enabled: String,
    var email: String,
    var email_verified: String?,
    var mobile_verified: String?,
    var country_id: String?,
    var linkedin_profile: String?,
    var company_name: String?,
    var company_logo: String?,
    var company_website: String?,
    var company_timeline_photo: String?,
    var company_about: String?,
    var company_linkedin: String?,
    var company_facebook: String?,
    var company_youtube: String?,
    var company_twitter: String?,
    var company_intro_video: String?,
)


package tech.merajobs.dataModel

data class EducationDataModel(
    val data : ArrayList<EducationData>? = null,
    val message: List<String>,
    val success: Boolean
)

data class EducationData(
    val course_id: Int,
    val course_name: String?,
    val course_type: Int,
    val course_type_label: String,
    val grading_system_lable: String,
    val from_year: Int,
    val grade: String,
    val id: Int,
    val job_seeker_id: Int,
    val qualification_id: Int,
    val qualification_name: String,
    val specialization: String,
    val specialization_id: Int,
    val to_year: Int,
    val university_id: Int,
    val university_name: String,
    val company_logo: String? = null
)
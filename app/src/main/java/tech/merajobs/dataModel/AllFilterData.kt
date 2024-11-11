package tech.merajobs.dataModel

data class AllFilterData(
    val success: String,
    val data: AllFilterList,
)

data class AllFilterList(
    val gender: ArrayList<Model1>,
    val employment_type: ArrayList<Model1>,
    val work_location_type: ArrayList<Model1>,
    val notice_period: ArrayList<Model1>,
    val status: ArrayList<Model1>,
    val currency: ArrayList<Model1>,
    val salary_range: ArrayList<Model1>,
    val salary_type: ArrayList<Model1>,
    val yes_no: ArrayList<Model1>,
    val job_seeking_status: ArrayList<Model1>,
    val marital_status: ArrayList<Model1>,
    val cast_category: ArrayList<Model1>,
    val usa_work_permit: ArrayList<Model1>,
    val work_shift: ArrayList<Model1>,
    val work_experience: ArrayList<Model1>,
    val language_proficiency: ArrayList<Model1>,
    val course_type: ArrayList<Model1>,
    val job_labels: ArrayList<Model1>,
    val social_accounts: ArrayList<Model1>,
)

data class Model1(
    val value: String,
    val label: String,
)

data class QualificationData(
    val success: String,
    val data: ArrayList<QualificationList>,
)

data class QualificationList(
    val id: String,
    val qualification: String,
)

data class CourseData(
    val success: String,
    val data: ArrayList<CourseList>,
)

data class CourseList(
    val id: String,
    val name: String,
    val qualification_id: String,
)

data class CourseTypeData(
    val success: String,
    val data: ArrayList<CourseTypeList>,
)

data class CourseTypeList(
    val id: String,
    val name: String,


    )

data class SpecializationData(
    val success: String,
    val data: ArrayList<SpecializationList>,
)

data class SpecializationList(
    val id: String,
    val course_id: String,
    val specialization: String,
    val qualification_id: String,
)

data class GradeSystemData(
    val success: String,
    val data: ArrayList<GradeSystemList>,
)

data class GradeSystemList(
    val id: String,
    val name: String,
)

data class UniversityData(
    val success: String,
    val data: ArrayList<UniversityList>,
)

data class UniversityList(
    val id: String,
    val university: String,
)

data class SkillData(
    val success: String,
    val data: ArrayList<SkillList>,
)

data class SkillList(
    val id: String,
    val skill_name: String,
)

data class LanguageListData(
    val success: String,
    val data: ArrayList<LanguageList>,
)

data class LanguageList(
    val id: String,
    val language: String,
)

data class ProficiencyListData(
    val success: String,
    val data: ArrayList<ProficiencyList>,
)

data class ProficiencyList(
    val id: String,
    val name: String,
)

data class IndustryDataModel(
    val success: String,
    val data: ArrayList<IndustryList>,
)

data class IndustryList(
    val id: String,
    val industryImage: String? = null,
    val industry_name: String,
    val job_count: String,
)

data class IndustryDepartmentDataModel(
    val success: String,
    val data: ArrayList<IndustryDepartmentList>,
)

data class IndustryDepartmentList(
    val id: String,
    val industry_id: String,
    val department_name: String,
)

data class IndustryDepartmentRoleDataModel(
    val success: String,
    val data: ArrayList<IndustryDepartmentRoleList>,
)

data class IndustryDepartmentRoleList(
    val id: String,
    val industry_id: String,
    val department_id: String,
    val role: String,
)

data class CountryDataModel(
    val success: String,
    val data: ArrayList<CountryList>,
)

data class CountryList(
    val id: String,
    val country_name: String,
    val code: String,
    val short_name: String,
    var status: Boolean = false,
)

data class StateDataModel(
    val success: String,
    val data: ArrayList<StateList>,
)

data class StateList(
    val id: String,
    val country_id: String,
    val state_name: String,
)


data class CityDataModel(
    val success: String,
    val data: ArrayList<CityList>,
)

data class CityList(
    val id: String,
    val country_id: String,
    val state_id: String,
    val city_name: String,
)

data class CompanyNameDataModel(
    val success: String,
    val data: ArrayList<CompanyNameList>,
)

data class CompanyNameList(
    val id: String,
    val company_name: String,
)

data class WorkLocationTypeDataModel(
    val success: String,
    val data: ArrayList<WorkLocationTypeList>,
)

data class WorkLocationTypeList(
    val id: String,
    val name: String,
)

data class EmploymentTypeDataModel(
    val success: String,
    val data: ArrayList<EmploymentTypeDataList>,
)

data class EmploymentTypeDataList(
    val id: String,
    val name: String,
)

data class JobLabelDataModel(
    val success: String,
    val data: ArrayList<JobLabelList>,
)

data class JobLabelList(
    val value: String,
    val label: String,
)
data class LocationDataModel(
    val success: String,
    val data: ArrayList<LocationList>,
)

data class LocationList(
    val location_id: String,
    val location: String,
    val job_count: String,
)

data class DepartmentRoleDataModel(
    val success: String,
    val data: ArrayList<DepartmentRoleList>,
)

data class DepartmentRoleList(
    val role_id: String,
    val role: String,
    val job_count: String,
)
data class SalaryDataModel(
    val success: String,
    val data: ArrayList<SalaryList>,
)

data class SalaryList(
    val id: String,
    val name: String,
)

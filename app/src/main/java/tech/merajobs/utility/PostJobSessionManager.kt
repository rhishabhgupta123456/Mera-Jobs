package tech.merajobs.utility

import android.content.Context
import android.content.SharedPreferences
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PostJobSessionManager(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("PostJobSessionManager", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()



    fun setJobId(item: String?) {
        editor.putString(AppConstant.JIB_ID, item)
        editor.apply()
    }

    fun getJobId(): String? {
        return sharedPreferences.getString(AppConstant.JIB_ID, null)
    }

      fun setTitle(item: String) {
        editor.putString(AppConstant.TITLE, item)
        editor.apply()
    }

    fun getTitle(): String {
        return sharedPreferences.getString(AppConstant.TITLE, "").toString()
    }

    fun setNoOFVacancy(item: String) {
        editor.putString(AppConstant.NO_OF_VACANCY, item)
        editor.apply()
    }

    fun getNoOFVacancy(): String {
        return sharedPreferences.getString(AppConstant.NO_OF_VACANCY, "").toString()
    }

    fun setJobLabel(item: String) {
        editor.putString(AppConstant.JOB_LABEL, item)
        editor.apply()
    }

    fun getJobLabel(): String {
        return sharedPreferences.getString(AppConstant.JOB_LABEL_LABEL, "").toString()
    }
    fun setJobLabelLabel(item: String) {
        editor.putString(AppConstant.JOB_LABEL_LABEL, item)
        editor.apply()
    }

    fun getJobLabelLabel(): String {
        return sharedPreferences.getString(AppConstant.JOB_LABEL, "").toString()
    }

    fun setIndustry(item: String) {
        editor.putString(AppConstant.INDUSTRY, item)
        editor.apply()
    }

    fun getIndustry(): String {
        return sharedPreferences.getString(AppConstant.INDUSTRY, "").toString()
    }

    fun setIndustryLabel(item: String) {
        editor.putString(AppConstant.INDUSTRY_LABEL, item)
        editor.apply()
    }

    fun getIndustryLabel(): String {
        return sharedPreferences.getString(AppConstant.INDUSTRY_LABEL, "").toString()
    }

    fun setDepartmentLabel(item: String) {
        editor.putString(AppConstant.DEPARTMENT_LABEL, item)
        editor.apply()
    }

    fun getDepartmentLabel(): String {
        return sharedPreferences.getString(AppConstant.DEPARTMENT_LABEL, "").toString()
    }

    fun setDepartment(item: String) {
        editor.putString(AppConstant.DEPARTMENT, item)
        editor.apply()
    }

    fun getDepartment(): String {
        return sharedPreferences.getString(AppConstant.DEPARTMENT, "").toString()
    }

    fun setRoleLabel(item: String) {
        editor.putString(AppConstant.ROLE_LABEL, item)
        editor.apply()
    }

    fun getRoleLabel(): String {
        return sharedPreferences.getString(AppConstant.ROLE_LABEL, "").toString()
    }

    fun setRole(item: String) {
        editor.putString(AppConstant.ROLE, item)
        editor.apply()
    }

    fun getRole(): String {
        return sharedPreferences.getString(AppConstant.ROLE, "").toString()
    }

    fun setSkill(item: ArrayList<String>?) {
        val gson = Gson()
        val jsonString = gson.toJson(item)
        editor.putString(AppConstant.SKILL, jsonString)
        editor.apply()
    }

    fun getSkill(): Array<String>? {
        val gson = Gson()
        val jsonString = sharedPreferences.getString(AppConstant.SKILL, null)
        if (jsonString != null) {
            val arrayType = object : TypeToken<Array<String>>() {}.type
            return gson.fromJson(jsonString, arrayType)
        } else {
            return null
        }
    }


    fun setGoodToHave(item: String) {
        editor.putString(AppConstant.GOOD_TO_HAVE, item)
        editor.apply()
    }

    fun getGoodToHave(): String {
        return sharedPreferences.getString(AppConstant.GOOD_TO_HAVE, "").toString()
    }

    fun setDescription(item: String) {
        editor.putString(AppConstant.DESCRIPTION, item)
        editor.apply()
    }

    fun getDescription(): String {
        return sharedPreferences.getString(AppConstant.DESCRIPTION, "").toString()
    }


    fun setLastDateToApply(item: String) {
        editor.putString(AppConstant.LAST_DATE_TO_APPLY, item)
        editor.apply()
    }

    fun getLastDateToApply(): String {
        return sharedPreferences.getString(AppConstant.LAST_DATE_TO_APPLY, "").toString()
    }


    fun setQualification(item: String) {
        editor.putString(AppConstant.QUALIFICATION, item)
        editor.apply()
    }

    fun getQualification(): String {
        return sharedPreferences.getString(AppConstant.QUALIFICATION, "").toString()
    }

    fun setQualificationLabel(item: String) {
        editor.putString(AppConstant.QUALIFICATION_LABEL, item)
        editor.apply()
    }

    fun getQualificationLabel(): String {
        return sharedPreferences.getString(AppConstant.QUALIFICATION_LABEL, "").toString()
    }

    fun setCourse(item: String) {
        editor.putString(AppConstant.COURSE, item)
        editor.apply()
    }

    fun getCourse(): String {
        return sharedPreferences.getString(AppConstant.COURSE, "").toString()
    }

    fun setCourseLabel(item: String) {
        editor.putString(AppConstant.COURSE_LABEL, item)
        editor.apply()
    }

    fun getCourseLabel(): String {
        return sharedPreferences.getString(AppConstant.COURSE_LABEL, "").toString()
    }

    fun setSpecialization(item: String) {
        editor.putString(AppConstant.SPECIALIZATION, item)
        editor.apply()
    }

    fun getSpecialization(): String {
        return sharedPreferences.getString(AppConstant.SPECIALIZATION, "").toString()
    }


    fun setSpecializationLabel(item: String) {
        editor.putString(AppConstant.SPECIALIZATION_LABEL, item)
        editor.apply()
    }

    fun getSpecializationLabel(): String {
        return sharedPreferences.getString(AppConstant.SPECIALIZATION_LABEL, "").toString()
    }

    fun setExperience(item: String) {
        editor.putString(AppConstant.EXPERIENCE, item)
        editor.apply()
    }

    fun getExperience(): String {
        return sharedPreferences.getString(AppConstant.EXPERIENCE, "").toString()
    }

    fun setExperienceLabel(item: String) {
        editor.putString(AppConstant.EXPERIENCE_LABEL, item)
        editor.apply()
    }

    fun getExperienceLabel(): String {
        return sharedPreferences.getString(AppConstant.EXPERIENCE_LABEL, "").toString()
    }

    fun setNoticePeriod(item: String) {
        editor.putString(AppConstant.NOTICE_PERIOD, item)
        editor.apply()
    }

    fun getNoticePeriod(): String {
        return sharedPreferences.getString(AppConstant.NOTICE_PERIOD, "").toString()
    }

    fun setNoticePeriodLabel(item: String) {
        editor.putString(AppConstant.NOTICE_PERIOD_LABEL, item)
        editor.apply()
    }

    fun getNoticePeriodLabel(): String {
        return sharedPreferences.getString(AppConstant.NOTICE_PERIOD_LABEL, "").toString()
    }

    fun setSalaryRange(item: String) {
        editor.putString(AppConstant.SALARY_RANGE, item)
        editor.apply()
    }

    fun getSalaryRange(): String {
        return sharedPreferences.getString(AppConstant.SALARY_RANGE, "").toString()
    }
    fun setSalaryRangeLabel(item: String) {
        editor.putString(AppConstant.SALARY_RANGE_LABEL, item)
        editor.apply()
    }

    fun getSalaryRangeLabel(): String {
        return sharedPreferences.getString(AppConstant.SALARY_RANGE_LABEL, "").toString()
    }


    fun setSalaryType(item: String) {
        editor.putString(AppConstant.SALARY_TYPE, item)
        editor.apply()
    }

    fun getSalaryType(): String {
        return sharedPreferences.getString(AppConstant.SALARY_TYPE, "").toString()
    }

    fun setSalaryTypeLabel(item: String) {
        editor.putString(AppConstant.SALARY_TYPE_LABEL, item)
        editor.apply()
    }

    fun getSalaryTypeLabel(): String {
        return sharedPreferences.getString(AppConstant.SALARY_TYPE_LABEL, "").toString()
    }

    fun setNegotiable(item: String) {
        editor.putString(AppConstant.NEGOTIABLE, item)
        editor.apply()
    }

    fun getNegotiable(): String {
        return sharedPreferences.getString(AppConstant.NEGOTIABLE, "").toString()
    }

   fun setNegotiableLabel(item: String) {
        editor.putString(AppConstant.NEGOTIABLE_LABEL, item)
        editor.apply()
    }

    fun getNegotiableLabel(): String {
        return sharedPreferences.getString(AppConstant.NEGOTIABLE_LABEL, "").toString()
    }

    fun setCurrency(item: String) {
        editor.putString(AppConstant.CURRENCY, item)
        editor.apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(AppConstant.CURRENCY, "").toString()
    }

   fun setCurrencyLabel(item: String) {
        editor.putString(AppConstant.CURRENCY_LABEL, item)
        editor.apply()
    }

    fun getCurrencyLabel(): String {
        return sharedPreferences.getString(AppConstant.CURRENCY_LABEL, "").toString()
    }

    fun setEmploymentType(item: String) {
        editor.putString(AppConstant.EMPLOYMENT_TYPE, item)
        editor.apply()
    }

    fun getEmploymentType(): String {
        return sharedPreferences.getString(AppConstant.EMPLOYMENT_TYPE, "").toString()
    }

   fun setEmploymentTypeLabel(item: String) {
        editor.putString(AppConstant.EMPLOYMENT_TYPE_LABEL, item)
        editor.apply()
    }

    fun getEmploymentTypeLabel(): String {
        return sharedPreferences.getString(AppConstant.EMPLOYMENT_TYPE_LABEL, "").toString()
    }

    fun setWorkLocationType(item: String) {
        editor.putString(AppConstant.WORK_LOCATION_TYPE, item)
        editor.apply()
    }

    fun getWorkLocationType(): String {
        return sharedPreferences.getString(AppConstant.WORK_LOCATION_TYPE, "").toString()
    }

  fun setWorkLocationTypeLabel(item: String) {
        editor.putString(AppConstant.WORK_LOCATION_TYPE_LABEL, item)
        editor.apply()
    }

    fun getWorkLocationTypeLabel(): String {
        return sharedPreferences.getString(AppConstant.WORK_LOCATION_TYPE_LABEL, "").toString()
    }

    fun setLocation(item: String) {
        editor.putString(AppConstant.LOCATION, item)
        editor.apply()
    }

    fun getLocation(): String {
        return sharedPreferences.getString(AppConstant.LOCATION, "").toString()
    }

   fun setLocationLabel(item: String) {
        editor.putString(AppConstant.LOCATION_LABEL, item)
        editor.apply()
    }

    fun getLocationLabel(): String {
        return sharedPreferences.getString(AppConstant.LOCATION_LABEL, "").toString()
    }


}
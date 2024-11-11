package tech.merajobs.networkModel.postJobViewModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent


class PostJobViewModel : ViewModel() {

    private val businessRepositoryImplement: PostJobRepositoryImplement =
        PostJobRepositoryImplement()

    fun getBranch(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getBranch(bearerToken)
    }
    fun getLocation(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getLocation(bearerToken)
    }
    fun postJob(
        bearerToken: String,
        title: String,
        description: String,
        experience: String,
        skills: List<Int>?,
        workLocationType: String,
        noticePeriod: String,
        industryId: String,
        departmentId: String,
        roleId: String,
        employmentType: String,
        qualificationId: String,
        courseId: String,
        specializationId: String,
        position: String,
        goodToHave: String,
        salaryRange: String,
        currency: String,
        lastApplyDate: String,
        isNegotiable: String,
        salaryType: String,
        location: String,
        jobLabel: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.postJob(
            bearerToken,
            title,
            description,
            experience,
            skills,
            workLocationType,
            noticePeriod,
            industryId,
            departmentId,
            roleId,
            employmentType,
            qualificationId,
            courseId,
            specializationId,
            position,
            goodToHave,
            salaryRange,
            currency,
            lastApplyDate,
            isNegotiable,
            salaryType,
            location,
            jobLabel,
        )
    }

   fun updateJobs(
        bearerToken: String,
        jobID: Int,
        title: String,
        description: String,
        experience: String,
        skills: List<Int>?,
        workLocationType: String,
        noticePeriod: String,
        industryId: String,
        departmentId: String,
        roleId: String,
        employmentType: String,
        qualificationId: String,
        courseId: String,
        specializationId: String,
        position: String,
        goodToHave: String,
        salaryRange: String,
        currency: String,
        lastApplyDate: String,
        isNegotiable: String,
        salaryType: String,
        location: String,
        jobLabel: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.updateJobs(
            bearerToken,
            jobID,
            title,
            description,
            experience,
            skills,
            workLocationType,
            noticePeriod,
            industryId,
            departmentId,
            roleId,
            employmentType,
            qualificationId,
            courseId,
            specializationId,
            position,
            goodToHave,
            salaryRange,
            currency,
            lastApplyDate,
            isNegotiable,
            salaryType,
            location,
            jobLabel,
        )
    }

    fun getSkillList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getSkillList(bearerToken)
    }


    fun getFilterDataList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getFilterDataList(bearerToken)
    }

    fun getIndustryList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getIndustryList(bearerToken)
    }


    fun getIndustryDepartmentList(
        bearerToken: String, industryId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getIndustryDepartmentList(bearerToken, industryId)
    }

    fun getIndustryDepartmentRoleList(
        bearerToken: String, departmentId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getIndustryDepartmentRoleList(bearerToken, departmentId)
    }


    fun getQualificationList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getQualificationList(bearerToken)
    }

    fun getCourseListList(
        bearerToken: String, qualificationId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getCourseListList(bearerToken, qualificationId)
    }

    fun getSpecializationList(
        bearerToken: String, courseId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getSpecializationList(bearerToken, courseId)
    }

}
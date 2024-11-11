package tech.merajobs.networkModel.homeViewModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent

class HomeViewModel : ViewModel() {

    private var homeRepositoryImplement: HomeRepositoryImplement =
        HomeRepositoryImplement()


    fun getUserProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getUserProfile(bearerToken)
    }

    fun getChatHistory(bearerToken: String, id: Int): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getChatHistory(bearerToken, id)
    }

    fun createChannelList(
        bearerToken: String,
        receiverID: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.createChannelList(bearerToken, receiverID)
    }


    fun sendMessage(
        bearerToken: String,
        id: Int,
        message: RequestBody?,
        attachment: MultipartBody.Part?,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.sendMessage(bearerToken, id, message, attachment)
    }

    fun clearAllChat(
        bearerToken: String,
        id: Int,
        toId: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.clearAllChat(bearerToken, id, toId)
    }

    fun getChannelList(bearerToken: String): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getChannelList(bearerToken)
    }


    fun deleteParticularMessage(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.deleteParticularMessage(bearerToken, id)
    }


    fun deleteChannel(
        bearerToken: String,
        channelIDs: List<Int>,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.deleteChannel(bearerToken, channelIDs)
    }


    fun getCategory(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getCategory(bearerToken)
    }

    fun getCompany(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getCompany(bearerToken)
    }

 fun getHomeCounter(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getHomeCounter(bearerToken)
    }

    fun getIndustry(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getIndustry(bearerToken)
    }

    fun getWorkLocationType(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getWorkLocationType(bearerToken)
    }

    fun getEmploymentType(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getEmploymentType(bearerToken)
    }

    fun getJobLabel(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getJobLabel(bearerToken)
    }

    fun getLocation(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getLocation(bearerToken)
    }

    fun getDepartmentRole(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getDepartmentRole(bearerToken)
    }

    fun getSkill(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getSkill(bearerToken)
    }

    fun getSalaryRange(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getSalaryRange(bearerToken)
    }

    fun getJobList(
        bearerToken: String,
        jobseekerId: Int,
        workLocationType: String,
        jobLabel: String,
        employmentType: String,
        location: String,
        role: String,
        skill: String,
        salaryRange: String,
        sortField: String,
        sortOrder: String,
        employerId: String,


        ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getJobList(
            bearerToken,
            jobseekerId,
            workLocationType,
            jobLabel,
            employmentType,
            location,
            role,
            skill,
            salaryRange,
            sortField,
            sortOrder,
            employerId
        )
    }

    fun getSavedJobList(
        bearerToken: String,
        searchKeyword: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.getSavedJobList(bearerToken, searchKeyword)
    }

    fun savedTheJob(
        bearerToken: String,
        jobID: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.savedTheJob(bearerToken, jobID)
    }

    fun applyTheJob(
        bearerToken: String,
        jobID: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.applyTheJob(bearerToken, jobID)
    }

    fun unSavedJob(
        bearerToken: String,
        jobID: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return homeRepositoryImplement.unSavedJob(bearerToken, jobID)
    }


}
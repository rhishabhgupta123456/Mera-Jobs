package tech.merajobs.networkModel.businessViewModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent


class BusinessViewModel : ViewModel() {

    private val businessRepositoryImplement: BusinessRepositoryImplement =
        BusinessRepositoryImplement()


    fun updateEmployerProfile(
        bearerToken: String,
        name: RequestBody?,
        companyName: RequestBody?,
        linkedinProfile: RequestBody?,
        companyWebsite: RequestBody?,
        countryId: RequestBody?,
        companyAbout: RequestBody?,
        companyFacebook: RequestBody?,
        companyYoutube: RequestBody?,
        companyTwitter: RequestBody?,
        companyIntroVideo: RequestBody?,
        photo: MultipartBody.Part?,
        companyLogo: MultipartBody.Part?,
        companyTimelinePhoto: MultipartBody.Part?,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.updateEmployerProfile(
            bearerToken,
            name,
            companyName,
            linkedinProfile,
            companyWebsite,
            countryId,
            companyAbout,
            companyFacebook,
            companyYoutube,
            companyTwitter,
            companyIntroVideo,
            photo,
            companyLogo,
            companyTimelinePhoto,
        )
    }

    fun getEmployerProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getEmployerProfile(bearerToken)
    }

    fun updateBranch(
        bearerToken: String,
        branchID: Int,
        branchName: String,
        address: String,
        zip: Int,
        countryId: Int,
        stateId: Int,
        cityId: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.updateBranch(
            bearerToken,
            branchID,
            branchName,
            address,
            zip,
            countryId,
            stateId,
            cityId
        )
    }

    fun addBranch(
        bearerToken: String,
        branchName: String,
        address: String,
        zip: Int,
        countryId: Int,
        stateId: Int,
        cityId: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.addBranch(
            bearerToken,
            branchName,
            address,
            zip,
            countryId,
            stateId,
            cityId
        )
    }

    fun getAllCountry(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getAllCountry(bearerToken)
    }

    fun getStateList(
        bearerToken: String,
        countryId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getStateList(bearerToken, countryId)
    }

    fun getCityList(
        bearerToken: String,
        stateID: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getCityList(bearerToken, stateID)
    }

    fun getBranch(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getBranch(bearerToken)
    }

    fun getPostedJobList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return businessRepositoryImplement.getPostedJobList(bearerToken)
    }


}
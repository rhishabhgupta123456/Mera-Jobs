package tech.merajobs.networkModel.settingNetworkModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent

class SettingViewModel : ViewModel() {

    private var settingRepositoryImplement: SettingRepositoryImplement =
        SettingRepositoryImplement()


    fun getUserProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.getUserProfile(bearerToken)
    }

    fun changeWhatsAppNotification(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.changeWhatsAppNotification(bearerToken,id)
    }


    fun getEmployerProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.getEmployerProfile(bearerToken)
    }

    fun emailUpdateRequest(
        bearerToken: String,
        email: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.emailUpdateRequest(bearerToken, email)
    }

    fun emailUpdateEmployerRequest(
        bearerToken: String,
        email: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.emailUpdateEmployerRequest(bearerToken, email)
    }

    fun emailUpdate(
        bearerToken: String,
        email: String,
        otp: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.emailUpdate(bearerToken, email, otp)
    }

    fun emailUpdateEmployer(
        bearerToken: String,
        email: String,
        otp: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.emailUpdateEmployer(bearerToken, email, otp)
    }

    fun mobileUpdateRequest(
        bearerToken: String,
        mobile: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.mobileUpdateRequest(bearerToken, mobile)
    }

    fun mobileUpdateRequestEmployer(
        bearerToken: String,
        mobile: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.mobileUpdateRequestEmployer(bearerToken, mobile)
    }

    fun mobileUpdate(
        bearerToken: String,
        mobile: String,
        otp: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.mobileUpdate(bearerToken, mobile, otp)
    }

    fun mobileUpdateEmployer(
        bearerToken: String,
        mobile: String,
        otp: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.mobileUpdateEmployer(bearerToken, mobile, otp)
    }

    fun changePassword(
        bearerToken: String,
        oldPassword: String,
        password: String,
        passwordConfirmation: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.changePassword(
            bearerToken,
            oldPassword,
            password,
            passwordConfirmation
        )
    }

    fun changePasswordEmployer(
        bearerToken: String,
        oldPassword: String,
        password: String,
        passwordConfirmation: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.changePasswordEmployer(
            bearerToken,
            oldPassword,
            password,
            passwordConfirmation
        )
    }

    fun notificationSetting(
        bearerToken: String,
        careerTipsNotification: Int,
        jobLookingStatus: Int,
        recommendedJobNotification: Int,
        jobAlertNotification: Int,
        applicationStatusNotification: Int,
        profileViewNotification: Int,
        profileStrengthNotification: Int,
        profileVisibility: Int,
        whatsappNotificationEnabled: Int,
        promotionsNotification: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.notificationSetting(
            bearerToken,
            careerTipsNotification,
            jobLookingStatus,
            recommendedJobNotification,
            jobAlertNotification,
            applicationStatusNotification,
            profileViewNotification,
            profileStrengthNotification,
            profileVisibility,
            whatsappNotificationEnabled,
            promotionsNotification,
        )
    }

    fun getEmployerList(
        bearerToken: String,
        companyName: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.getEmployerList(
            bearerToken,
            companyName
        )
    }

    fun getBlockCompanyList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.getBlockCompanyList(
            bearerToken,
        )
    }
    fun getCMS(
        cmsKey: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.getCMS(
            cmsKey,
        )
    }

    fun addBlockCompany(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.addBlockCompany(
            bearerToken,
            id
        )
    }

    fun deleteBlockCompany(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return settingRepositoryImplement.deleteBlockCompany(
            bearerToken,
            id
        )
    }


}
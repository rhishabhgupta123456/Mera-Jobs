package tech.merajobs.networkModel.authNetworkPannel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent


class AuthViewModel : ViewModel() {

    private val authRepositoryImplement: AuthRepositoryImplement = AuthRepositoryImplement()


    fun login(
        emailOrPhone: String,
        getPassword: String,
        userType: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.login(emailOrPhone, getPassword, userType)
    }

    fun register(
        name: String,
        mobile: String,
        userType: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.register(name, mobile, userType)


    }

    fun employerRegister(
        name: String,
        mobile: String,
        companyName: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.employerRegister(name, mobile, companyName)
    }

    fun otpVerification(
        otp: String,
        mobile: String,
        password: String,
        passwordConfirmation: String,
        userType: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.otpVerification(
            otp,
            mobile,
            password,
            passwordConfirmation,
            userType
        )
    }

    fun socialLogIn(
        name: String,
        email: String,
        socialType: String,
        socialId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.socialLogIn(name, email, socialType, socialId)
    }

    fun getCMS(
        cmsKey: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.getCMS(cmsKey)
    }


    fun forgetPassword(
        mobile: String,
        userType: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.forgetPassword(mobile, userType)
    }


    fun resetPassword(
        mobile: String,
        otp: String,
        password: String,
        passwordConfirmation: String,
        userType: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return authRepositoryImplement.resetPassword(
            mobile,
            otp,
            password,
            passwordConfirmation,
            userType
        )
    }

}
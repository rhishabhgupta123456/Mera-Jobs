package tech.merajobs.networkModel

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import tech.merajobs.utility.AppConstant
import java.util.concurrent.TimeUnit


class ApiInterface {

    private var apiRequest: ApiRequest

    init {

        val headerInterceptor = Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestWithHeaders: Request =
                originalRequest.newBuilder().addHeader("Accept", "application/json").build()
            chain.proceed(requestWithHeaders)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }


        val okHttpClient = OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS).writeTimeout(40, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor).build()

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder().baseUrl(AppConstant.API_BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        apiRequest = retrofit.create(ApiRequest::class.java)


    }

    fun signIn(emailOrPhone: String, password: String, userType: String): Call<JsonObject?> {
        return apiRequest.signIn(emailOrPhone, password, userType)
    }

    fun register(
        name: String, mobile: String, userType: String,
    ): Call<JsonObject?> {
        return apiRequest.register(name, mobile, userType)
    }

    fun employerRegister(
        name: String, mobile: String, companyName: String,
    ): Call<JsonObject?> {
        return apiRequest.employerRegister(name, mobile, companyName)
    }

    fun otpVerification(
        otp: String,
        mobile: String,
        password: String,
        passwordConfirmation: String,
        userType: String,
    ): Call<JsonObject?> {
        return apiRequest.otpVerification(otp, mobile, password, passwordConfirmation, userType)
    }

    fun socialLogIn(
        name: String,
        email: String,
        socialType: String,
        socialId: String,
    ): Call<JsonObject?> {
        return apiRequest.socialLogIn(name, email, socialType, socialId)
    }

    fun createChannelList(
        bearerToken: String,
        receiverID: String,
    ): Call<JsonObject?> {
        return apiRequest.createChannelList(bearerToken, receiverID)
    }

    fun forgetPassword(mobile: String, userType: String): Call<JsonObject?> {
        return apiRequest.forgetPassword(mobile, userType)
    }

    fun resetPassword(
        mobile: String,
        otp: String,
        password: String,
        passwordConfirmation: String,
        userType: String,
    ): Call<JsonObject?> {
        return apiRequest.resetPassword(mobile, otp, password, passwordConfirmation, userType)
    }

    fun getChatHistory(bearerToken: String, id: Int): Call<JsonObject?> {
        return apiRequest.getChatHistory(bearerToken, id)
    }

    fun sendMessage(
        bearerToken: String,
        id: Int,
        message: RequestBody?,
        attachment: MultipartBody.Part?,
    ): Call<JsonObject?> {
        return apiRequest.sendMessage(bearerToken, id, message, attachment)
    }


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
    ): Call<JsonObject?> {
        return apiRequest.updateEmployerProfile(
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

    fun clearAllChat(
        bearerToken: String,
        id: Int,
        toId: Int,
    ): Call<JsonObject?> {
        return apiRequest.clearAllChat(bearerToken, id, toId)
    }

    fun deleteParticularMessage(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteParticularMessage(bearerToken, id)
    }


    fun deleteChannel(
        bearerToken: String,
        channelIDs: List<Int>,
    ): Call<JsonObject?> {
        return apiRequest.deleteChannel(bearerToken, channelIDs)
    }


    fun getUserProfile(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getUserProfile(bearerToken)
    }


    fun changeWhatsAppNotification(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.changeWhatsAppNotification(bearerToken, id)
    }

    fun getEmployerProfile(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getEmployerProfile(bearerToken)
    }

    fun getCategory(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCategory(bearerToken)
    }

    fun getCompany(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCompany(bearerToken)
    }

   fun getHomeCounter(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getHomeCounter(bearerToken)
    }

    fun getIndustry(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getIndustry(bearerToken)
    }

    fun getWorkLocationType(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getWorkLocationType(bearerToken)
    }

    fun getEmploymentType(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getEmploymentType(bearerToken)
    }

    fun getJobLabel(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getJobLabel(bearerToken)
    }

    fun getLocation(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getLocation(bearerToken)
    }

    fun getDepartmentRole(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getDepartmentRole(bearerToken)
    }


    fun getCVDetails(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCVDetails(bearerToken)
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
    ): Call<JsonObject?> {
        return apiRequest.getJobList(
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

    fun getCMS(
        cmsKey: String,
    ): Call<JsonObject?> {
        return apiRequest.getCMS(cmsKey)
    }

    fun getFilterDataList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getFilterDataList(bearerToken)
    }

    fun savedTheJob(
        bearerToken: String,
        jobID: Int,
    ): Call<JsonObject?> {
        return apiRequest.savedTheJob(bearerToken, jobID)
    }

    fun applyTheJob(
        bearerToken: String,
        jobID: Int,
    ): Call<JsonObject?> {
        return apiRequest.applyTheJob(bearerToken, jobID)
    }

    fun getCompanyNameList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCompanyNameList(bearerToken)
    }

    fun getSavedJobList(
        bearerToken: String,
        searchKeyword: String,
    ): Call<JsonObject?> {
        return apiRequest.getSavedJobList(bearerToken, searchKeyword)
    }


    fun getIndustryList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getIndustryList(bearerToken)
    }

    fun getAllCountry(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getAllCountry(bearerToken)
    }

    fun getStateList(
        bearerToken: String,
        countryId: String,
    ): Call<JsonObject?> {
        return apiRequest.getStateList(bearerToken, countryId)
    }

    fun getCityList(
        bearerToken: String,
        stateId: String,
    ): Call<JsonObject?> {
        return apiRequest.getCityList(bearerToken, stateId)
    }

    fun getSkillList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getSkillList(bearerToken)
    }

    fun getQualificationList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getQualificationList(bearerToken)
    }

    fun getCourseListList(
        bearerToken: String,
        qualificationId: String,
    ): Call<JsonObject?> {
        return apiRequest.getCourseListList(bearerToken, qualificationId)
    }

    fun getIndustryDepartmentList(
        bearerToken: String,
        industryId: String,
    ): Call<JsonObject?> {
        return apiRequest.getIndustryDepartmentList(bearerToken, industryId)
    }

    fun getCourseTypeList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCourseTypeList(bearerToken)
    }

    fun getSpecializationList(
        bearerToken: String,
        courseId: String,
    ): Call<JsonObject?> {
        return apiRequest.getSpecializationList(bearerToken, courseId)
    }

    fun getIndustryDepartmentRoleList(
        bearerToken: String,
        departmentId: String,
    ): Call<JsonObject?> {
        return apiRequest.getIndustryDepartmentRoleList(bearerToken, departmentId)
    }

    fun getGradeSystemSpinner(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getGradeSystemSpinner(bearerToken)
    }

    fun getUniversityList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getUniversityList(bearerToken)
    }

    fun getSalaryRange(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getSalaryRange(bearerToken)
    }

    fun getSkills(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getSkills(bearerToken)
    }

    fun getLanguageList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getLanguageList(bearerToken)
    }

    fun getLanguageProficiency(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getLanguageProficiency(bearerToken)
    }

    fun getWorkingExperience(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getWorkingExperience(bearerToken)
    }

    fun getEducation(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getEducation(bearerToken)
    }

    fun getBranch(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getBranch(bearerToken)
    }

    fun getPostedJobList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getPostedJobList(bearerToken)
    }

    fun getITSkills(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getITSkills(bearerToken)
    }

    fun getLanguage(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getLanguage(bearerToken)
    }

    fun addWorkExperience(
        bearerToken: String,
        currentlyWorking: String,
        employmentType: String,
        totalExpYears: String,
        totalExpMonths: String,
        company: String,
        jobTitle: String,
        joiningDate: String,
        exitDate: String,
        jobProfile: String,
        skills: List<Int>,
        noticePeriod: String,
    ): Call<JsonObject?> {
        return apiRequest.addWorkExperience(
            bearerToken,
            currentlyWorking,
            employmentType,
            totalExpYears,
            totalExpMonths,
            company,
            jobTitle,
            joiningDate,
            exitDate,
            jobProfile,
            skills,
            noticePeriod
        )
    }


    fun addLanguage(
        bearerToken: String,
        languageId: Int,
        proficiency: Int,
        read: Int,
        speak: Int,
        write: Int,
    ): Call<JsonObject?> {
        return apiRequest.addLanguage(
            bearerToken,
            languageId,
            proficiency,
            read,
            speak,
            write,
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
    ): Call<JsonObject?> {
        return apiRequest.addBranch(
            bearerToken,
            branchName,
            address,
            zip,
            countryId,
            stateId,
            cityId
        )
    }

    fun updateLanguage(
        bearerToken: String,
        id: Int,
        languageId: Int,
        proficiency: Int,
        read: Int,
        speak: Int,
        write: Int,
    ): Call<JsonObject?> {
        return apiRequest.updateLanguage(
            bearerToken,
            id,
            languageId,
            proficiency,
            read,
            speak,
            write,
        )
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
    ): Call<JsonObject?> {
        return apiRequest.updateBranch(
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

    fun deleteLanguage(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteLanguage(
            bearerToken,
            id,
        )
    }

    fun unSavedJob(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.unSavedJob(
            bearerToken,
            id,
        )
    }

    fun addEducation(
        bearerToken: String,
        qualificationId: String,
        courseId: String,
        specializationId: String,
        courseType: String,
        universityId: String,
        fromYear: String,
        toYear: String,
        gradingSystem: String,
        grade: String,
    ): Call<JsonObject?> {
        return apiRequest.addEducation(
            bearerToken,
            qualificationId,
            courseId,
            specializationId,
            courseType,
            universityId,
            fromYear,
            toYear,
            gradingSystem,
            grade,
        )

    }


    fun addSkills(
        bearerToken: String,
        skillId: String,
        version: String,
        experienceYears: String,
        experienceMonths: String?,
        lastUsedYear: String,
    ): Call<JsonObject?> {
        return apiRequest.addSkills(
            bearerToken,
            skillId,
            version,
            experienceYears,
            experienceMonths,
            lastUsedYear,
        )
    }

    fun updateSkills(
        bearerToken: String,
        id: Int,
        skillId: String,
        version: String,
        experienceYears: String,
        experienceMonths: String,
        lastUsedYear: String,
    ): Call<JsonObject?> {
        return apiRequest.updateSkills(
            bearerToken,
            id,
            skillId,
            version,
            experienceYears,
            experienceMonths,
            lastUsedYear,
        )
    }

    fun deleteSkills(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteSkills(
            bearerToken,
            id,
        )
    }

    fun updateEducation(
        bearerToken: String,
        id: Int,
        qualificationId: String,
        courseId: String,
        specializationId: String,
        courseType: String,
        universityId: String,
        fromYear: String,
        toYear: String,
        gradingSystem: String,
        grade: String,
    ): Call<JsonObject?> {
        return apiRequest.updateEducation(
            bearerToken,
            id,
            qualificationId,
            courseId,
            specializationId,
            courseType,
            universityId,
            fromYear,
            toYear,
            gradingSystem,
            grade,
        )
    }


    fun updateJobPreference(
        bearerToken: String,
        industryId: Int,
        departmentId: Int,
        roleId: Int,
        employmentType: Int,
        workMode: Int,
        expectedCtc: String,
    ): Call<JsonObject?> {
        return apiRequest.updateJobPreference(
            bearerToken,
            industryId,
            departmentId,
            roleId,
            employmentType,
            workMode,
            expectedCtc,
        )
    }

    fun updatePersonalInformation(
        bearerToken: String,
        maritalStatus: Int,
        usaWorkPermit: Int,
        otherCountriesWorkPermit: List<Int>,
        dob: String,
        address: String,
        zip: Int,
        castCategory: Int,
        countryId: Int,
        stateId: Int,
        cityId: Int,
    ): Call<JsonObject?> {
        return apiRequest.updatePersonalInformation(
            bearerToken,
            maritalStatus,
            usaWorkPermit,
            otherCountriesWorkPermit,
            dob,
            address,
            zip,
            castCategory,
            countryId,
            stateId,
            cityId
        )
    }

    fun deleteEducation(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteEducation(
            bearerToken,
            id,
        )
    }


    fun updateWorkExperience(
        bearerToken: String,
        id: Int,
        currentlyWorking: String,
        employmentType: String,
        totalExpYears: String,
        totalExpMonths: String,
        company: String,
        jobTitle: String,
        joiningDate: String,
        exitDate: String,
        jobProfile: String,
        skills: List<Int>,
        noticePeriod: String,
    ): Call<JsonObject?> {
        return apiRequest.updateWorkExperience(
            bearerToken,
            id,
            currentlyWorking,
            employmentType,
            totalExpYears,
            totalExpMonths,
            company,
            jobTitle,
            joiningDate,
            exitDate,
            jobProfile,
            skills,
            noticePeriod
        )
    }

    fun updateProfileSummary(
        bearerToken: String,
        updateProfileSummary: String,
    ): Call<JsonObject?> {
        return apiRequest.updateProfileSummary(
            bearerToken, updateProfileSummary
        )
    }

    fun updateResumeHeadline(
        bearerToken: String,
        name: String,
        resumeHeadLine: String,
    ): Call<JsonObject?> {
        return apiRequest.updateResumeHeadline(
            bearerToken,
            name,
            resumeHeadLine,
        )
    }

    fun deleteResume(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.deleteResume(bearerToken)
    }

    fun deleteWorkExperience(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteWorkExperience(bearerToken, id)
    }

    fun deleteProfile(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.deleteProfile(bearerToken)
    }

    fun updateResume(bearerToken: String, updateResume: MultipartBody.Part?): Call<JsonObject?> {
        return apiRequest.updateResume(bearerToken, updateResume)
    }

    fun updateProfilePicture(
        bearerToken: String, profilePicture: MultipartBody.Part?,
    ): Call<JsonObject?> {
        return apiRequest.updateProfilePicture(bearerToken, profilePicture)
    }

    fun emailUpdateRequest(
        bearerToken: String, email: String,
    ): Call<JsonObject?> {
        return apiRequest.emailUpdateRequest(bearerToken, email)
    }

    fun emailUpdateEmployerRequest(
        bearerToken: String, email: String,
    ): Call<JsonObject?> {
        return apiRequest.emailUpdateEmployerRequest(bearerToken, email)
    }

    fun emailUpdate(
        bearerToken: String, email: String, otp: String,
    ): Call<JsonObject?> {
        return apiRequest.emailUpdate(bearerToken, email, otp)
    }

    fun emailUpdateEmployer(
        bearerToken: String, email: String, otp: String,
    ): Call<JsonObject?> {
        return apiRequest.emailUpdateEmployer(bearerToken, email, otp)
    }

    fun mobileUpdateRequest(
        bearerToken: String, mobile: String,
    ): Call<JsonObject?> {
        return apiRequest.mobileUpdateRequest(bearerToken, mobile)
    }

    fun mobileUpdateRequestEmployer(
        bearerToken: String, mobile: String,
    ): Call<JsonObject?> {
        return apiRequest.mobileUpdateRequestEmployer(bearerToken, mobile)
    }

    fun mobileUpdate(
        bearerToken: String, mobile: String, otp: String,
    ): Call<JsonObject?> {
        return apiRequest.mobileUpdate(bearerToken, mobile, otp)
    }


    fun mobileUpdateEmployer(
        bearerToken: String, mobile: String, otp: String,
    ): Call<JsonObject?> {
        return apiRequest.mobileUpdateEmployer(bearerToken, mobile, otp)
    }


    fun changePassword(
        bearerToken: String,
        oldPassword: String,
        password: String,
        passwordConfirmation: String,
    ): Call<JsonObject?> {
        return apiRequest.changePassword(
            bearerToken, oldPassword, password, passwordConfirmation
        )
    }

    fun changePasswordEmployer(
        bearerToken: String,
        oldPassword: String,
        password: String,
        passwordConfirmation: String,
    ): Call<JsonObject?> {
        return apiRequest.changePasswordEmployer(
            bearerToken, oldPassword, password, passwordConfirmation
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
    ): Call<JsonObject?> {
        return apiRequest.notificationSetting(
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
    ): Call<JsonObject?> {
        return apiRequest.getEmployerList(
            bearerToken,
            companyName,
        )
    }

    fun getBlockCompanyList(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getBlockCompanyList(
            bearerToken,
        )
    }


    fun addBlockCompany(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.addBlockCompany(
            bearerToken, id
        )
    }

    fun deleteBlockCompany(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteBlockCompany(
            bearerToken, id
        )
    }


    // ------------------  Accomplishment --------------------- Network Model --------------------------------------


    fun getOnlineProfile(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getOnlineProfile(
            bearerToken,
        )
    }


    fun addOnlineProfile(
        bearerToken: String,
        accountType: String,
        url: String,
        description: String,
    ): Call<JsonObject?> {
        return apiRequest.addOnlineProfile(
            bearerToken, accountType, url, description
        )
    }

    fun updateOnlineProfile(
        bearerToken: String,
        id: Int,
        accountType: String,
        url: String,
        description: String,
    ): Call<JsonObject?> {
        return apiRequest.updateOnlineProfile(
            bearerToken, id, accountType, url, description
        )
    }

    fun deleteOnlineProfile(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteOnlineProfile(
            bearerToken,
            id,
        )
    }

    fun getWorkSample(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getWorkSample(
            bearerToken,
        )
    }

    fun addWorkSample(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        durationYear: String,
        durationMonth: String,
    ): Call<JsonObject?> {
        return apiRequest.addWorkSample(
            bearerToken, title, url, description, durationYear, durationMonth
        )
    }

    fun updateWorkSample(
        bearerToken: String,
        id: Int,
        title: String,
        url: String,
        description: String,
        durationYear: String,
        durationMonth: String,
    ): Call<JsonObject?> {
        return apiRequest.updateWorkSample(
            bearerToken, id, title, url, description, durationYear, durationMonth
        )
    }

    fun getChannelList(bearerToken: String): Call<JsonObject?> {
        return apiRequest.getChannelList(bearerToken)
    }

    fun deleteWorkSample(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteWorkSample(
            bearerToken,
            id,
        )
    }

    fun getWhitePaper(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getWhitePaper(
            bearerToken,
        )
    }

    fun addWhitePaper(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        publishedDate: String,
    ): Call<JsonObject?> {
        return apiRequest.addWhitePaper(
            bearerToken,
            title,
            url,
            description,
            publishedDate,
        )
    }

    fun updateWhitePaper(
        bearerToken: String,
        id: Int,
        title: String,
        url: String,
        description: String,
        publishedDate: String,
    ): Call<JsonObject?> {
        return apiRequest.updateWhitePaper(
            bearerToken,
            id,
            title,
            url,
            description,
            publishedDate,
        )
    }

    fun deleteWhitePaper(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteWhitePaper(
            bearerToken,
            id,
        )
    }


    fun getPresentation(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getPresentation(
            bearerToken,
        )
    }

    fun addPresentation(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
    ): Call<JsonObject?> {
        return apiRequest.addPresentation(
            bearerToken,
            title,
            url,
            description,
        )
    }

    fun updatePresentation(
        bearerToken: String,
        id: Int,
        title: String,
        url: String,
        description: String,
    ): Call<JsonObject?> {
        return apiRequest.updatePresentation(
            bearerToken,
            id,
            title,
            url,
            description,
        )
    }

    fun deletePresentation(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deletePresentation(
            bearerToken,
            id,
        )
    }


    fun getPatent(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getPatent(
            bearerToken,
        )
    }


    fun addPatent(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        applicationNo: String,
        issuedDte: String,
        office: String,
    ): Call<JsonObject?> {
        return apiRequest.addPatent(
            bearerToken, title, url, description, applicationNo, issuedDte, office
        )
    }

    fun updatePatent(
        bearerToken: String,
        id: Int,
        title: String,
        url: String,
        description: String,
        applicationNo: String,
        issuedDte: String,
        office: String,
    ): Call<JsonObject?> {
        return apiRequest.updatePatent(
            bearerToken, id, title, url, description, applicationNo, issuedDte, office
        )
    }

    fun deletePatent(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deletePatent(
            bearerToken,
            id,
        )
    }

    fun getCertificate(
        bearerToken: String,
    ): Call<JsonObject?> {
        return apiRequest.getCertificate(
            bearerToken,
        )
    }


    fun addCertificate(
        bearerToken: String,
        certificate: String,
        institute: String,
        validTill: String,
        certificateNo: String,
    ): Call<JsonObject?> {
        return apiRequest.addCertificate(
            bearerToken,
            certificate,
            institute,
            validTill,
            certificateNo,
        )
    }

    fun updateCertificate(
        bearerToken: String,
        id: Int,
        certificate: String,
        institute: String,
        validTill: String,
        certificateNo: String,
    ): Call<JsonObject?> {
        return apiRequest.updateCertificate(
            bearerToken,
            id,
            certificate,
            institute,
            validTill,
            certificateNo,
        )
    }

    fun deleteCertificate(
        bearerToken: String,
        id: Int,
    ): Call<JsonObject?> {
        return apiRequest.deleteCertificate(
            bearerToken,
            id,
        )
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
    ): Call<JsonObject?> {
        return apiRequest.postJob(
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
    ): Call<JsonObject?> {
        return apiRequest.updateJobs(
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


}


interface ApiRequest {


    @POST("auth")
    @FormUrlEncoded
    fun signIn(
        @Field("username") emailOrPhone: String,
        @Field("password") password: String,
        @Field("user_type") userType: String,
    ): Call<JsonObject?>


    @POST("auth/register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("user_type") userType: String,
    ): Call<JsonObject?>

    @POST("auth/register/employer")
    @FormUrlEncoded
    fun employerRegister(
        @Field("name") name: String,
        @Field("mobile") mobile: String,
        @Field("company_name") companyName: String,
    ): Call<JsonObject?>


    @POST("auth/otp-verification")
    @FormUrlEncoded
    fun otpVerification(
        @Field("otp") otp: String,
        @Field("mobile_email") mobile: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("user_type") userType: String,
    ): Call<JsonObject?>


    @POST("auth/social-login")
    @FormUrlEncoded
    fun socialLogIn(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("account_type") socialType: String,
        @Field("social_auth_id") socialId: String,
    ): Call<JsonObject?>

    @POST("message/channel")
    @FormUrlEncoded
    fun createChannelList(
        @Header("Authorization") bearerToken: String,
        @Field("to_id") stateId: String,
    ): Call<JsonObject?>


    @POST("auth/forgot-password")
    @FormUrlEncoded
    fun forgetPassword(
        @Field("mobile") mobile: String,
        @Field("user_type") userType: String,
    ): Call<JsonObject?>


    @POST("auth/reset-password")
    @FormUrlEncoded
    fun resetPassword(
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
        @Field("user_type") userType: String,
    ): Call<JsonObject?>

    @GET("jobseeker/profile")
    fun getUserProfile(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @PATCH("employer/settings/whatsapp_notification_enabled/{id}")
    fun changeWhatsAppNotification(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>

    @GET("employer/profile")
    fun getEmployerProfile(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @GET("message/message/{id}")
    fun getChatHistory(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @Multipart
    @POST("message/message/{id}")
    fun sendMessage(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Part("message") message: RequestBody?,
        @Part attachment: MultipartBody.Part?,
    ): Call<JsonObject?>

    @Multipart
    @POST("employer/profile")
    fun updateEmployerProfile(
        @Header("Authorization") bearerToken: String,
        @Part("name") name: RequestBody?,
        @Part("company_name") companyName: RequestBody?,
        @Part("linkedin_profile") linkedinProfile: RequestBody?,
        @Part("company_website") companyWebsite: RequestBody?,
        @Part("country_id") countryId: RequestBody?,
        @Part("company_about") companyAbout: RequestBody?,
        @Part("company_facebook") companyFacebook: RequestBody?,
        @Part("company_youtube") companyYoutube: RequestBody?,
        @Part("company_twitter") companyTwitter: RequestBody?,
        @Part("company_intro_video") companyIntroVideo: RequestBody?,
        @Part photo: MultipartBody.Part?,
        @Part companyLogo: MultipartBody.Part?,
        @Part companyTimelinePhoto: MultipartBody.Part?
    ): Call<JsonObject?>


    @DELETE("message/clear_chat/{id}")
    fun clearAllChat(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Query("to_id") toId: Int,
    ): Call<JsonObject?>

    @DELETE("message/message/{id}")
    fun deleteParticularMessage(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @DELETE("message/channel")
    fun deleteChannel(
        @Header("Authorization") bearerToken: String,
        @Query("channel_ids[]") channelIDs: List<Int>,
    ): Call<JsonObject?>


    @GET("web/top-categories")
    fun getCategory(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("web/top-companies")
    fun getCompany(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

  @GET("data/jobs-count")
    fun getHomeCounter(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/industry")
    fun getIndustry(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/work-location-type")
    fun getWorkLocationType(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/employment-type")
    fun getEmploymentType(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/job-labels")
    fun getJobLabel(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("web/top-locations")
    fun getLocation(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("web/top-categories")
    fun getDepartmentRole(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("jobseeker/resume-check")
    fun getCVDetails(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("web/jobs")
    fun getJobList(
        @Header("Authorization") bearerToken: String,
        @Query("jobseeker_id") jobseekerId: Int,
        @Query("work_location_type") workLocationType: String,
        @Query("job_label") jobLabel: String,
        @Query("employment_type") employmentType: String,
        @Query("location") location: String,
        @Query("role") role: String,
        @Query("skill") skill: String,
        @Query("salary_range") salaryRange: String,
        @Query("sort_field") sortField: String,
        @Query("sort_order") sortOrder: String,
        @Query("employer_id") employerId: String,
    ): Call<JsonObject?>

    @GET("data/cms")
    fun getCMS(
        @Query("cms_key") cmsKey: String,
    ): Call<JsonObject?>

    @GET("jobseeker/saved-job")
    fun getSavedJobList(
        @Header("Authorization") bearerToken: String,
        @Query("search_keyword") searchKeyword: String,
    ): Call<JsonObject?>


    @POST("jobseeker/resume")
    @Multipart
    fun updateResume(
        @Header("Authorization") bearerToken: String, @Part resume: MultipartBody.Part?,
    ): Call<JsonObject?>

    @DELETE("jobseeker/resume")
    fun deleteResume(@Header("Authorization") bearerToken: String): Call<JsonObject?>

    @DELETE("jobseeker/photo")
    fun deleteProfile(@Header("Authorization") bearerToken: String): Call<JsonObject?>


    @POST("jobseeker/photo")
    @Multipart
    fun updateProfilePicture(
        @Header("Authorization") bearerToken: String, @Part photo: MultipartBody.Part?,
    ): Call<JsonObject?>


    @GET("data/all")
    fun getFilterDataList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/saved-job")
    @FormUrlEncoded
    fun savedTheJob(
        @Header("Authorization") bearerToken: String,
        @Field("job_id") jobId: Int,
    ): Call<JsonObject?>


    @POST("jobseeker/applied-job")
    @FormUrlEncoded
    fun applyTheJob(
        @Header("Authorization") bearerToken: String,
        @Field("job_id") jobId: Int,
    ): Call<JsonObject?>

    @GET("data/company")
    fun getCompanyNameList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/industry")
    fun getIndustryList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/country")
    fun getAllCountry(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/state")
    fun getStateList(
        @Header("Authorization") bearerToken: String,
        @Query("country_id") countryId: String,
    ): Call<JsonObject?>


    @GET("data/city")
    fun getCityList(
        @Header("Authorization") bearerToken: String,
        @Query("state_id") stateId: String,
    ): Call<JsonObject?>

    @GET("data/skills")
    fun getSkillList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @GET("data/qualifications")
    fun getQualificationList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/course")
    fun getCourseListList(
        @Header("Authorization") bearerToken: String,
        @Query("qualification_id") qualificationId: String,
    ): Call<JsonObject?>

    @GET("data/industry-departments")
    fun getIndustryDepartmentList(
        @Header("Authorization") bearerToken: String,
        @Query("industry_id") industryId: String,
    ): Call<JsonObject?>


    @GET("data/course-type")
    fun getCourseTypeList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/specializations")
    fun getSpecializationList(
        @Header("Authorization") bearerToken: String,
        @Query("course_id") qualificationId: String,
    ): Call<JsonObject?>

    @GET("data/department-roles")
    fun getIndustryDepartmentRoleList(
        @Header("Authorization") bearerToken: String,
        @Query("department_id") departmentId: String,
    ): Call<JsonObject?>

    @GET("data/grading-systems")
    fun getGradeSystemSpinner(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/universities")
    fun getUniversityList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/salary-range")
    fun getSalaryRange(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @GET("data/skills")
    fun getSkills(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/languages")
    fun getLanguageList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("data/language-proficiency")
    fun getLanguageProficiency(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @GET("jobseeker/work-experience")
    fun getWorkingExperience(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("jobseeker/education")
    fun getEducation(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("employer/branch")
    fun getBranch(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("employer/job")
    fun getPostedJobList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("jobseeker/skills")
    fun getITSkills(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("jobseeker/language")
    fun getLanguage(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @POST("jobseeker/work-experience")
    @FormUrlEncoded
    fun addWorkExperience(
        @Header("Authorization") bearerToken: String,
        @Field("currently_working") currentlyWorking: String,
        @Field("employment_type") employmentType: String,
        @Field("total_exp_years") totalExpYears: String,
        @Field("total_exp_months") totalExpMonths: String,
        @Field("company") company: String,
        @Field("job_title") jobTitle: String,
        @Field("joning_date") joiningDate: String,
        @Field("exit_date") exitDate: String,
        @Field("job_profile") jobProfile: String,
        @Field("skills[]") skills: List<Int>,
        @Field("notice_period") noticePeriod: String,
    ): Call<JsonObject?>


    @POST("employer/branch")
    @FormUrlEncoded
    fun addBranch(
        @Header("Authorization") bearerToken: String,
        @Field("branch_name") branchName: String,
        @Field("address") address: String,
        @Field("zip") zip: Int,
        @Field("country_id") countryId: Int,
        @Field("state_id") stateId: Int,
        @Field("city_id") cityId: Int,
    ): Call<JsonObject?>


    @POST("jobseeker/language")
    @FormUrlEncoded
    fun addLanguage(
        @Header("Authorization") bearerToken: String,
        @Field("language_id") languageId: Int,
        @Field("proficiency") proficiency: Int,
        @Field("read") read: Int,
        @Field("speak") speak: Int,
        @Field("write") write: Int,
    ): Call<JsonObject?>


    @PATCH("jobseeker/language/{id}")
    @FormUrlEncoded
    fun updateLanguage(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("language_id") languageId: Int,
        @Field("proficiency") proficiency: Int,
        @Field("read") read: Int,
        @Field("speak") speak: Int,
        @Field("write") write: Int,
    ): Call<JsonObject?>


    @PUT("employer/branch/{id}")
    @FormUrlEncoded
    fun updateBranch(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("branch_name") branchName: String,
        @Field("address") address: String,
        @Field("zip") zip: Int,
        @Field("country_id") countryId: Int,
        @Field("state_id") stateID: Int,
        @Field("city_id") cityId: Int,
    ): Call<JsonObject?>


    @DELETE("jobseeker/language/{id}")
    fun deleteLanguage(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @DELETE("jobseeker/saved-job/{id}")
    fun unSavedJob(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @POST("jobseeker/education")
    @FormUrlEncoded
    fun addEducation(
        @Header("Authorization") bearerToken: String,
        @Field("qualification_id") qualificationId: String,
        @Field("course_id") courseId: String,
        @Field("specialization_id") specializationId: String,
        @Field("course_type") courseType: String,
        @Field("university_id") universityId: String,
        @Field("from_year") fromYear: String,
        @Field("to_year") toYear: String,
        @Field("grading_system") gradingSystem: String,
        @Field("grade") grade: String,
    ): Call<JsonObject?>


    @POST("jobseeker/skills")
    @FormUrlEncoded
    fun addSkills(
        @Header("Authorization") bearerToken: String,
        @Field("skill_id") skillId: String,
        @Field("version") version: String,
        @Field("experience_years") experienceYears: String,
        @Field("experience_months") experienceMonths: String?,
        @Field("last_used_year") lastUsedYear: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/skills/{id}")
    @FormUrlEncoded
    fun updateSkills(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("skill_id") skillId: String,
        @Field("version") version: String,
        @Field("experience_years") experienceYears: String,
        @Field("experience_months") experienceMonths: String,
        @Field("last_used_year") lastUsedYear: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/skills/{id}")
    fun deleteSkills(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @PATCH("jobseeker/work-experience/{id}")
    @FormUrlEncoded
    fun updateWorkExperience(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("currently_working") currentlyWorking: String,
        @Field("employment_type") employmentType: String,
        @Field("total_exp_years") totalExpYears: String,
        @Field("total_exp_months") totalExpMonths: String,
        @Field("company") company: String,
        @Field("job_title") jobTitle: String,
        @Field("joning_date") joiningDate: String,
        @Field("exit_date") exitDate: String,
        @Field("job_profile") jobProfile: String,
        @Field("skills[]") skills: List<Int>,
        @Field("notice_period") noticePeriod: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/education/{id}")
    @FormUrlEncoded
    fun updateEducation(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("qualification_id") qualificationId: String,
        @Field("course_id") courseId: String,
        @Field("specialization_id") specializationId: String,
        @Field("course_type") courseType: String,
        @Field("university_id") universityId: String,
        @Field("from_year") fromYear: String,
        @Field("to_year") toYear: String,
        @Field("grading_system") gradingSystem: String,
        @Field("grade") grade: String,
    ): Call<JsonObject?>

    @PATCH("jobseeker/career-profile")
    @FormUrlEncoded
    fun updateJobPreference(
        @Header("Authorization") bearerToken: String,
        @Field("industry_id") industryId: Int,
        @Field("department_id") departmentId: Int,
        @Field("role_id") roleId: Int,
        @Field("employment_type") employmentType: Int,
        @Field("work_mode") workMode: Int,
        @Field("expected_ctc") expectedCtc: String,
    ): Call<JsonObject?>

    @PATCH("jobseeker/personal-details")
    @FormUrlEncoded
    fun updatePersonalInformation(
        @Header("Authorization") bearerToken: String,
        @Field("marital_status") maritalStatus: Int,
        @Field("usa_work_permit") usaWorkPermit: Int,
        @Field("other_countries_work_permit[]") otherCountriesWorkPermit: List<Int>,
        @Field("dob") dob: String,
        @Field("address") address: String,
        @Field("zip") zip: Int,
        @Field("cast_category") castCategory: Int,
        @Field("country_id") countryId: Int,
        @Field("state_id") stateId: Int,
        @Field("city_id") cityId: Int,
    ): Call<JsonObject?>


    @DELETE("jobseeker/work-experience/{id}")
    fun deleteWorkExperience(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @DELETE("jobseeker/education/{id}")
    fun deleteEducation(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @PATCH("jobseeker/profile-summary")
    @FormUrlEncoded
    fun updateProfileSummary(
        @Header("Authorization") bearerToken: String,
        @Field("profile_summary") skills: String,
    ): Call<JsonObject?>

    @PATCH("jobseeker/resume-headline")
    @FormUrlEncoded
    fun updateResumeHeadline(
        @Header("Authorization") bearerToken: String,
        @Field("name") name: String,
        @Field("resume_headline") resumeHeadline: String,
    ): Call<JsonObject?>


    @POST("jobseeker/email-update-request")
    @FormUrlEncoded
    fun emailUpdateRequest(
        @Header("Authorization") bearerToken: String,
        @Field("email") email: String,
    ): Call<JsonObject?>

    @POST("employer/email-update-request")
    @FormUrlEncoded
    fun emailUpdateEmployerRequest(
        @Header("Authorization") bearerToken: String,
        @Field("email") email: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/email-update")
    @FormUrlEncoded
    fun emailUpdate(
        @Header("Authorization") bearerToken: String,
        @Field("email") email: String,
        @Field("otp") otp: String,
    ): Call<JsonObject?>

    @PATCH("employer/email-update")
    @FormUrlEncoded
    fun emailUpdateEmployer(
        @Header("Authorization") bearerToken: String,
        @Field("email") email: String,
        @Field("otp") otp: String,
    ): Call<JsonObject?>


    @POST("jobseeker/mobile-update-request")
    @FormUrlEncoded
    fun mobileUpdateRequest(
        @Header("Authorization") bearerToken: String,
        @Field("mobile") mobile: String,
    ): Call<JsonObject?>

    @POST("employer/mobile-update-request")
    @FormUrlEncoded
    fun mobileUpdateRequestEmployer(
        @Header("Authorization") bearerToken: String,
        @Field("mobile") mobile: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/mobile-update")
    @FormUrlEncoded
    fun mobileUpdate(
        @Header("Authorization") bearerToken: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
    ): Call<JsonObject?>

    @PATCH("employer/mobile-update")
    @FormUrlEncoded
    fun mobileUpdateEmployer(
        @Header("Authorization") bearerToken: String,
        @Field("mobile") mobile: String,
        @Field("otp") otp: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/change-password")
    @FormUrlEncoded
    fun changePassword(
        @Header("Authorization") bearerToken: String,
        @Field("old_password") oldPassword: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): Call<JsonObject?>

    @PATCH("employer/change-password")
    @FormUrlEncoded
    fun changePasswordEmployer(
        @Header("Authorization") bearerToken: String,
        @Field("old_password") oldPassword: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/notification-settings")
    @FormUrlEncoded
    fun notificationSetting(
        @Header("Authorization") bearerToken: String,
        @Field("career_tips_notification") careerTipsNotification: Int,
        @Field("job_looking_status") jobLookingStatus: Int,
        @Field("recommended_job_notification") recommendedJobNotification: Int,
        @Field("job_alert_notification") jobAlertNotification: Int,
        @Field("application_status_notification") applicationStatusNotification: Int,
        @Field("profile_view_notification") profileViewNotification: Int,
        @Field("profile_strength_notification") profileStrengthNotification: Int,
        @Field("profile_visibility") profileVisibility: Int,
        @Field("whatsapp_notification_enabled") whatsappNotificationEnabled: Int,
        @Field("promotions_notification") promotionsNotification: Int,
    ): Call<JsonObject?>

    @GET("jobseeker/employer-search")
    fun getEmployerList(
        @Header("Authorization") bearerToken: String,
        @Query("company_name") companyName: String,
    ): Call<JsonObject?>

    @GET("jobseeker/block-company")
    fun getBlockCompanyList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>


    @POST("jobseeker/block-company")
    @FormUrlEncoded
    fun addBlockCompany(
        @Header("Authorization") bearerToken: String,
        @Field("company_id") companyId: Int,
    ): Call<JsonObject?>


    @DELETE("jobseeker/block-company/{id}")
    fun deleteBlockCompany(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    // ------------------  Accomplishment --------------------- Network Model --------------------------------------


    @GET("jobseeker/online-presence")
    fun getOnlineProfile(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/online-presence")
    @FormUrlEncoded
    fun addOnlineProfile(
        @Header("Authorization") bearerToken: String,
        @Field("account_type") accountType: String,
        @Field("url") url: String,
        @Field("description") description: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/online-presence/{id}")
    @FormUrlEncoded
    fun updateOnlineProfile(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("account_type") accountType: String,
        @Field("url") url: String,
        @Field("description") description: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/online-presence/{id}")
    fun deleteOnlineProfile(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @GET("jobseeker/work-sample")
    fun getWorkSample(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/work-sample")
    @FormUrlEncoded
    fun addWorkSample(
        @Header("Authorization") bearerToken: String,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("duration_year") durationYear: String,
        @Field("duration_months") durationMonth: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/work-sample/{id}")
    @FormUrlEncoded
    fun updateWorkSample(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("duration_year") durationYear: String,
        @Field("duration_months") durationMonth: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/work-sample/{id}")
    fun deleteWorkSample(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @GET("jobseeker/research")
    fun getWhitePaper(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/research")
    @FormUrlEncoded
    fun addWhitePaper(
        @Header("Authorization") bearerToken: String,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("published_date") publishedDate: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/research/{id}")
    @FormUrlEncoded
    fun updateWhitePaper(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("published_date") publishedDate: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/research/{id}")
    fun deleteWhitePaper(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>

    @GET("jobseeker/presentation")
    fun getPresentation(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @GET("message/channel")
    fun getChannelList(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/presentation")
    @FormUrlEncoded
    fun addPresentation(
        @Header("Authorization") bearerToken: String,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/presentation/{id}")
    @FormUrlEncoded
    fun updatePresentation(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/presentation/{id}")
    fun deletePresentation(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @GET("jobseeker/patent")
    fun getPatent(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/patent")
    @FormUrlEncoded
    fun addPatent(
        @Header("Authorization") bearerToken: String,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("application_no") applicationNo: String,
        @Field("issued_date") issuedDte: String,
        @Field("office") office: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/patent/{id}")
    @FormUrlEncoded
    fun updatePatent(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("url") url: String,
        @Field("description") description: String,
        @Field("application_no") applicationNo: String,
        @Field("issued_date") issuedDte: String,
        @Field("office") office: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/patent/{id}")
    fun deletePatent(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @GET("jobseeker/certificate")
    fun getCertificate(
        @Header("Authorization") bearerToken: String,
    ): Call<JsonObject?>

    @POST("jobseeker/certificate")
    @FormUrlEncoded
    fun addCertificate(
        @Header("Authorization") bearerToken: String,
        @Field("certificate") certificate: String,
        @Field("institute") institute: String,
        @Field("valid_till") validTill: String,
        @Field("certificate_no") certificateNo: String,
    ): Call<JsonObject?>


    @PATCH("jobseeker/certificate/{id}")
    @FormUrlEncoded
    fun updateCertificate(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("certificate") certificate: String,
        @Field("institute") institute: String,
        @Field("valid_till") validTill: String,
        @Field("certificate_no") certificateNo: String,
    ): Call<JsonObject?>


    @DELETE("jobseeker/certificate/{id}")
    fun deleteCertificate(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
    ): Call<JsonObject?>


    @POST("employer/job")
    @FormUrlEncoded
    fun postJob(
        @Header("Authorization") bearerToken: String,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("experience") experience: String,
        @Field("skills[]") skills: List<Int>?,
        @Field("work_location_type") workLocationType: String,
        @Field("notice_period") noticePeriod: String,
        @Field("industry_id") industryId: String,
        @Field("department_id") departmentId: String,
        @Field("role_id") roleId: String,
        @Field("employment_type") employmentType: String,
        @Field("qualification_id") qualificationId: String,
        @Field("course_id") courseId: String,
        @Field("specialization_id") specializationId: String,
        @Field("position") position: String,
        @Field("good_to_have") goodToHave: String,
        @Field("salary_range") salaryRange: String,
        @Field("currency") currency: String,
        @Field("last_apply_date") lastApplyDate: String,
        @Field("is_negotiable") isNegotiable: String,
        @Field("salary_type") salaryType: String,
        @Field("location") location: String,
        @Field("job_label") jobLabel: String,
    ): Call<JsonObject?>

    @PATCH("employer/job/{id}")
    @FormUrlEncoded
    fun updateJobs(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("experience") experience: String,
        @Field("skills[]") skills: List<Int>?,
        @Field("work_location_type") workLocationType: String,
        @Field("notice_period") noticePeriod: String,
        @Field("industry_id") industryId: String,
        @Field("department_id") departmentId: String,
        @Field("role_id") roleId: String,
        @Field("employment_type") employmentType: String,
        @Field("qualification_id") qualificationId: String,
        @Field("course_id") courseId: String,
        @Field("specialization_id") specializationId: String,
        @Field("position") position: String,
        @Field("good_to_have") goodToHave: String,
        @Field("salary_range") salaryRange: String,
        @Field("currency") currency: String,
        @Field("last_apply_date") lastApplyDate: String,
        @Field("is_negotiable") isNegotiable: String,
        @Field("salary_type") salaryType: String,
        @Field("location") location: String,
        @Field("job_label") jobLabel: String,
    ): Call<JsonObject?>


}
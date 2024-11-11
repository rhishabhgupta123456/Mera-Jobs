package tech.merajobs.networkModel.profileNetworkModel

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import tech.merajobs.networkModel.BaseResponse
import tech.merajobs.networkModel.SingleLiveEvent


class ProfileViewModel : ViewModel() {

    private val profileRepositoryImplement: ProfileRepositoryImplement =
        ProfileRepositoryImplement()


    fun getUserProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getUserProfile(bearerToken)
    }

    fun getCVDetails(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCVDetails(bearerToken)
    }

    fun getFilterDataList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getFilterDataList(bearerToken)
    }

    fun getCompanyNameList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCompanyNameList(bearerToken)
    }

    fun getIndustryList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getIndustryList(bearerToken)
    }

    fun getAllCountry(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getAllCountry(bearerToken)
    }

    fun getStateList(
        bearerToken: String,
        countryId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getStateList(bearerToken, countryId)
    }

    fun getCityList(
        bearerToken: String,
        stateID: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCityList(bearerToken, stateID)
    }


    fun getSkillList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getSkillList(bearerToken)
    }

    fun getQualificationList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getQualificationList(bearerToken)
    }

    fun getCourseListList(
        bearerToken: String, qualificationId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCourseListList(bearerToken, qualificationId)
    }

    fun getIndustryDepartmentList(
        bearerToken: String, industryId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getIndustryDepartmentList(bearerToken, industryId)
    }

    fun getCourseTypeList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCourseTypeList(bearerToken)
    }

    fun getSpecializationList(
        bearerToken: String, courseId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getSpecializationList(bearerToken, courseId)
    }

    fun getIndustryDepartmentRoleList(
        bearerToken: String, departmentId: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getIndustryDepartmentRoleList(bearerToken, departmentId)
    }

    fun getGradeSystemSpinner(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getGradeSystemSpinner(bearerToken)
    }


    fun getUniversityList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getUniversityList(bearerToken)
    }

    fun getSkills(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getSkills(bearerToken)
    }

    fun getLanguageList(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getLanguageList(bearerToken)
    }

    fun getLanguageProficiency(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getLanguageProficiency(bearerToken)
    }


    fun deleteResume(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteResume(bearerToken)
    }


    fun updateProfileSummary(
        bearerToken: String,
        updateProfileSummary: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateProfileSummary(bearerToken, updateProfileSummary)
    }

    fun updateResumeHeadline(
        bearerToken: String,
        name: String,
        resumeHeadLine: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateResumeHeadline(bearerToken, name, resumeHeadLine)
    }

    fun deleteProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteProfile(bearerToken)
    }

    fun updateResume(
        bearerToken: String, pdfPart: MultipartBody.Part?,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateResume(bearerToken, pdfPart)
    }

    fun updateProfilePicture(
        bearerToken: String,
        profilePicture: MultipartBody.Part?,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateProfilePicture(bearerToken, profilePicture)
    }

    fun getWorkingExperience(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getWorkingExperience(bearerToken)
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addWorkExperience(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addLanguage(
            bearerToken,
            languageId,
            proficiency,
            read,
            speak,
            write,
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateLanguage(
            bearerToken,
            id,
            languageId,
            proficiency,
            read,
            speak,
            write,
        )
    }

    fun deleteLanguage(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteLanguage(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateWorkExperience(
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

    fun deleteWorkExperience(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteWorkExperience(bearerToken, id)
    }

    fun getEducation(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getEducation(bearerToken)
    }

    fun getITSkills(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getITSkills(bearerToken)
    }

    fun getLanguage(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getLanguage(bearerToken)
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addEducation(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addSkills(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateSkills(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteSkills(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateEducation(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateJobPreference(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updatePersonalInformation(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteEducation(bearerToken, id)
    }


    // ------------------  Accomplishment --------------------- Network Model --------------------------------------


    // <---------- Online Profile ------->

    fun getOnlineProfile(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getOnlineProfile(bearerToken)
    }

    fun addOnlineProfile(
        bearerToken: String,
        accountType: String,
        url: String,
        description: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addOnlineProfile(
            bearerToken, accountType, url, description
        )
    }

    fun updateOnlineProfile(
        bearerToken: String,
        id: Int,
        accountType: String,
        url: String,
        description: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateOnlineProfile(
            bearerToken, id, accountType, url, description
        )
    }

    fun deleteOnlineProfile(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteOnlineProfile(bearerToken, id)
    }


    // <---------- Work Sample ------->

    fun getWorkSample(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getWorkSample(bearerToken)
    }

    fun addWorkSample(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        durationYear: String,
        durationMonth: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addWorkSample(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateWorkSample(
            bearerToken, id, title, url, description, durationYear, durationMonth
        )
    }

    fun deleteWorkSample(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteWorkSample(bearerToken, id)
    }


    // <---------- White Paper ------->

    fun getWhitePaper(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getWhitePaper(bearerToken)
    }

    fun addWhitePaper(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        publishedDate: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addWhitePaper(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateWhitePaper(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteWhitePaper(bearerToken, id)
    }

    // <---------- Presentation ------->

    fun getPresentation(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getPresentation(bearerToken)
    }

    fun addPresentation(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addPresentation(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updatePresentation(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deletePresentation(bearerToken, id)
    }

    // <---------- Patents ------->

    fun getPatent(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getPatent(bearerToken)
    }

    fun addPatent(
        bearerToken: String,
        title: String,
        url: String,
        description: String,
        applicationNo: String,
        issuedDte: String,
        office: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addPatent(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updatePatent(
            bearerToken, id, title, url, description, applicationNo, issuedDte, office
        )
    }

    fun deletePatent(
        bearerToken: String,
        id: Int,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deletePatent(bearerToken, id)
    }


    // <---------- Certificate ------->


    fun getCertificate(
        bearerToken: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.getCertificate(bearerToken)
    }

    fun addCertificate(
        bearerToken: String,
        certificate: String,
        institute: String,
        validTill: String,
        certificateNo: String,
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.addCertificate(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.updateCertificate(
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
    ): SingleLiveEvent<BaseResponse<JsonObject>> {
        return profileRepositoryImplement.deleteCertificate(bearerToken, id)
    }

}
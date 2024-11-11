package tech.merajobs.utility

import android.content.Context
import android.content.SharedPreferences

class SessionManager(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MeraJobsAppTempStorage", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()


    fun setLanguage(language: String) {
        editor.putString(AppConstant.LANGUAGE, language)
        editor.apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString(AppConstant.LANGUAGE, AppConstant.ENGLISH).toString()
    }

    fun setSourceFragment(sourceFragment: String) {
        editor.putString(AppConstant.SOURCE_FRAGMENT, sourceFragment)
        editor.apply()
    }

    fun getSourceFragment(): String {
        return sharedPreferences.getString(AppConstant.SOURCE_FRAGMENT, AppConstant.ENGLISH)
            .toString()
    }


    fun setBearerToken(token: String?) {
        editor.putString("token", token)
        editor.apply()
    }

    fun getBearerToken(): String {
        return "Bearer " + sharedPreferences.getString("token", "").toString()
    }

    fun setUserType(userType: String?) {
        editor.putString("userType", userType)
        editor.apply()
    }

    fun getUserType(): String {
        return sharedPreferences.getString("userType", "").toString()
    }

    fun setUserID(userId: Int) {
        editor.putInt("userId", userId)
        editor.apply()
    }

    fun getUserID(): Int {
        val userId = sharedPreferences.getInt("userId", 0)
        return if (userId != 0) {
            userId
        } else {
            0
        }
    }

    fun setUserName(name: String?) {
        editor.putString("name", name)
        editor.apply()
    }

    fun getUserName(): String {
        return sharedPreferences.getString("name", "").toString()
    }

    fun setUserEmail(email: String?) {
        editor.putString("email", email)
        editor.apply()
    }

    fun getUserEmail(): String {
        return sharedPreferences.getString("email", "").toString()
    }

    fun setUserPhone(phone: String?) {
        editor.putString("phone", phone)
        editor.apply()
    }

    fun getUserPhone(): String {
        return sharedPreferences.getString("phone", "").toString()
    }

    fun setUserWhatsAppNotification(whatsAppNotification: String?) {
        editor.putString("WhatsAppNotification", whatsAppNotification)
        editor.apply()
    }

    fun getUserWhatsAppNotification(): String {
        return sharedPreferences.getString("WhatsAppNotification", "0").toString()
    }

    fun setUserProfile(profile: String?) {
        editor.putString("profile", profile)
        editor.apply()
    }

    fun getUserProfile(): String {
        return sharedPreferences.getString("profile", "").toString()
    }

    fun setUserProfileVerify(profile: String?) {
        editor.putString("profileVerify", profile)
        editor.apply()
    }

    fun getUserProfileVerify(): String {
        return sharedPreferences.getString("profileVerify", "0").toString()
    }

    fun deleteData() {
        editor.clear()
        editor.commit()
        editor.apply()
    }

}
package tech.merajobs.utility

import android.content.Context
import android.content.res.Configuration
import java.util.*


object LanguageChange {

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Save to shared preferences
        val editor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("AppLanguage", language)
        editor.apply()
    }

    fun loadLocale(context: Context) {
        val prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = prefs.getString("AppLanguage", "")
        if (language != null) {
            setLocale(context, language)
        }
    }

}
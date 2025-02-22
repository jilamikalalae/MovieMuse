package com.example.moviemuse

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.Locale

object LocaleManager {

    private const val LANGUAGE_KEY = "language_key"

    fun setLocale(context: Context): Context {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val languageCode = sharedPreferences.getString(LANGUAGE_KEY, "en") ?: "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }
}

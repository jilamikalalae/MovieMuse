package com.example.moviemuse

import android.content.Context
import android.content.res.Configuration
import android.preference.PreferenceManager
import java.util.Locale

object LocaleManager {

    private const val LANGUAGE_KEY = "language_key"
    private const val DEFAULT_LANGUAGE = "en"

    fun setLocale(context: Context): Context {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // Ensure English is used if no language is saved
        val languageCode = sharedPreferences.getString(LANGUAGE_KEY, null) ?: run {
            sharedPreferences.edit().putString(LANGUAGE_KEY, DEFAULT_LANGUAGE).apply()
            DEFAULT_LANGUAGE
        }

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

    fun getSavedLanguage(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }
}


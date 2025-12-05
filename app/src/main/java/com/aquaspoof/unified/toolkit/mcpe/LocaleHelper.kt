package com.aquaspoof.unified.toolkit.mcpe

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LocaleHelper {

    const val PREFS_NAME = "app_prefs"
    const val PREF_KEY_LANGUAGE = "selected_language"

    const val LANG_SYSTEM = "system"
    const val LANG_ENGLISH = "en"
    const val LANG_RUSSIAN = "ru"
    const val LANG_UKRAINIAN = "uk"
    const val LANG_BELARUSIAN = "be"
    const val LANG_CHINESE_S = "zh-CN"
    const val LANG_CHINESE_T = "zh-TW"

    fun applySavedLocale(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val language = prefs.getString(PREF_KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
        setLocale(language)
    }

    fun setLocale(languageCode: String) {
        val localeList = when (languageCode) {
            LANG_ENGLISH -> LocaleListCompat.forLanguageTags(LANG_ENGLISH)
            LANG_RUSSIAN -> LocaleListCompat.forLanguageTags(LANG_RUSSIAN)
            LANG_UKRAINIAN -> LocaleListCompat.forLanguageTags(LANG_UKRAINIAN)
            LANG_BELARUSIAN -> LocaleListCompat.forLanguageTags(LANG_BELARUSIAN)
            LANG_CHINESE_S -> LocaleListCompat.forLanguageTags("zh-Hans-CN")
            LANG_CHINESE_T -> LocaleListCompat.forLanguageTags("zh-Hant-TW")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun saveLocalePreference(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(PREF_KEY_LANGUAGE, languageCode)
        }
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_KEY_LANGUAGE, LANG_SYSTEM) ?: LANG_SYSTEM
    }
}
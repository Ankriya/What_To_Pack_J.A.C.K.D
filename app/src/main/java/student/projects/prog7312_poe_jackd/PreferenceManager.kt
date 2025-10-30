package student.projects.prog7312_poe_jackd

import android.content.Context

object PreferenceManager {
    private const val PREFS_NAME = "app_prefs"
    private const val LANGUAGE_KEY = "language_key"

    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, "en") ?: "en"
    }
}

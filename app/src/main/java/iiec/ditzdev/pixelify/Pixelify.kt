package iiec.ditzdev.pixelify

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import iiec.ditzdev.pixelify.handler.CrashHandler

class Pixelify : Application() {

    companion object {
        const val USER_PREFS: String = "user_prefs"
        const val PREFS_SMART_ALERTS: String = "prefs_smart_alert"
        const val THEME_MODE: String = "theme_mode"
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandler.initialize(this)
        applyThemeMode()
    }

    private fun applyThemeMode() {
        val prefs: SharedPreferences = getSharedPreferences(USER_PREFS, MODE_PRIVATE)
        val themeMode = prefs.getInt(THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}
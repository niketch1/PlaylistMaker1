package com.example.playlistmaker1

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val THEME_PREFERENCES = "themePreferences"
const val EDIT_BOOLEAN_KEY = "editBooleanKey"
var darkTheme = false

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(EDIT_BOOLEAN_KEY, darkThemeCheck(this))
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun darkThemeCheck(context: Context):Boolean{

        var isNight = false
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        isNight = when ((currentNightMode)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }
}
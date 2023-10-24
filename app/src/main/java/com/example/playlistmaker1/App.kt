package com.example.playlistmaker1

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker1.di.dataModule
import com.example.playlistmaker1.di.interactorModule
import com.example.playlistmaker1.di.repositoryModule
import com.example.playlistmaker1.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val THEME_PREFERENCES = "themePreferences"
const val EDIT_BOOLEAN_KEY = "editBooleanKey"
var darkTheme = false

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        appContext = applicationContext
        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(EDIT_BOOLEAN_KEY, darkThemeCheck())
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

    fun darkThemeCheck():Boolean{

        var isNight = false
        val currentNightMode = appContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        isNight = when ((currentNightMode)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
        return isNight
    }

    companion object {
        lateinit var appContext: Context
    }
}
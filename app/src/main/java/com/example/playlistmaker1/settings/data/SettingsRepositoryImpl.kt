package com.example.playlistmaker1.settings.data

import android.content.Context
import com.example.playlistmaker1.App
import com.example.playlistmaker1.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(val app: App) : SettingsRepository{

    override fun getThemeSettings(): ThemeSettings {
       return when (app.darkThemeCheck()){
           true -> ThemeSettings.DARK_THEME
           false -> ThemeSettings.LIGHT_THEME
       }
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        app.switchTheme(when (settings){
            ThemeSettings.DARK_THEME-> true
            ThemeSettings.LIGHT_THEME-> false
        })
    }
}
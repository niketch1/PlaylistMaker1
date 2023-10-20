package com.example.playlistmaker1.settings.domain.api

import com.example.playlistmaker1.settings.domain.model.ThemeSettings

interface SettingsInteractor{
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}
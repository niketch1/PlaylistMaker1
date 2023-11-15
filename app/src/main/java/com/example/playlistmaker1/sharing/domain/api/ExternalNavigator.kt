package com.example.playlistmaker1.sharing.domain.api

import com.example.playlistmaker1.sharing.domain.model.EmailData

interface ExternalNavigator {

    fun shareLink(link : String?)
    fun openLink(agreement : String?)
    fun openEmail(emailData: EmailData?)

}
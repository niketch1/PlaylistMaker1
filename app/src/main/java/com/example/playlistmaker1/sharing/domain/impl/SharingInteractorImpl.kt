package com.example.playlistmaker1.sharing.domain.impl

import com.example.playlistmaker1.App
import com.example.playlistmaker1.R
import com.example.playlistmaker1.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker1.sharing.domain.api.SharingInteractor
import com.example.playlistmaker1.sharing.domain.model.EmailData


class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    val context = App.appContext

    override fun shareApp(){
        externalNavigator.shareLink(getShareLink())
    }
    override fun openTerms(){
        externalNavigator.openLink(getTermsLink())
    }
    override fun openSupport(){
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareLink() : String?{
        return context.resources?.getString(R.string.linkCourse)
    }
    private fun getTermsLink() : String?{
        return context.resources?.getString(R.string.offer)
    }
    private fun getSupportEmailData() : EmailData {
        return EmailData(
            email = context.resources?.getString(R.string.email),
            messageSup = context.resources?.getString(R.string.messageSup),
            theme = context.resources?.getString(R.string.theme)
        )
    }
}
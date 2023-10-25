package com.example.playlistmaker1.sharing.data

import android.content.Intent
import android.net.Uri
import com.example.playlistmaker1.App
import com.example.playlistmaker1.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker1.sharing.domain.model.EmailData

class ExternalNavigatorImpl() : ExternalNavigator {

    val context = App.appContext


    override fun shareLink(link: String?){
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(chooserIntent)

    }

    override fun openLink(agreement: String?) {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(agreement)
        agreementIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(agreementIntent)
    }

    override fun openEmail(emailData: EmailData?) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, emailData?.email)
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData?.theme)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData?.messageSup)
        supportIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(supportIntent)
    }
}
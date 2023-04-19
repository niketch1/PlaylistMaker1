package com.example.playlistmaker1

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val back = findViewById<FrameLayout>(R.id.buttonBack)

        back.setOnClickListener {
            finish()
        }


        val share = findViewById<ImageView>(R.id.buttonShare)

        share.setOnClickListener {
            val message = getString(R.string.linkCourse)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, null)
            startActivity(chooserIntent)
        }

        val support = findViewById<ImageView>(R.id.buttonSupport)

        support.setOnClickListener {
            val messageSup = getString(R.string.messageSup)
            val theme = getString(R.string.theme)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, theme)
            supportIntent.putExtra(Intent.EXTRA_TEXT, messageSup)
            startActivity(supportIntent)
        }

        val agreement = findViewById<ImageView>(R.id.buttonAgreement)

        agreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(getString(R.string.offer))
            startActivity(agreementIntent)
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setChecked(darkTheme)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}
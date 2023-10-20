package com.example.playlistmaker1.settings.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker1.App
import com.example.playlistmaker1.R
import com.example.playlistmaker1.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity() : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]
        val back = findViewById<FrameLayout>(R.id.buttonBack)

        back.setOnClickListener {
            finish()
        }

        val share = findViewById<ImageView>(R.id.buttonShare)

        share.setOnClickListener {
            viewModel.shareApp()
        }

        val support = findViewById<ImageView>(R.id.buttonSupport)

        support.setOnClickListener {
            viewModel.openSupport()
        }

        val agreement = findViewById<ImageView>(R.id.buttonAgreement)

        agreement.setOnClickListener {
            viewModel.openTerms()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.setChecked(viewModel.getTheme())
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
            App.context = this
        }
    }
}
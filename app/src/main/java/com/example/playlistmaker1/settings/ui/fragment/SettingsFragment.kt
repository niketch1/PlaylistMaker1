package com.example.playlistmaker1.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.playlistmaker1.App
import com.example.playlistmaker1.databinding.FragmentSettingsBinding
import com.example.playlistmaker1.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val settingsViewModel by viewModel<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var share: ImageView
    private lateinit var support: ImageView
    private lateinit var agreement: ImageView
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        share = binding.buttonShare
        support = binding.buttonSupport
        agreement = binding.buttonAgreement
        themeSwitcher = binding.themeSwitcher

        share.setOnClickListener{
            settingsViewModel.shareApp()
        }

        support.setOnClickListener{
            settingsViewModel.openSupport()
        }

        agreement.setOnClickListener{
            settingsViewModel.openTerms()
        }

        themeSwitcher.setChecked(settingsViewModel.getTheme())
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            settingsViewModel.changeTheme(checked)
            App.appContext= requireContext()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
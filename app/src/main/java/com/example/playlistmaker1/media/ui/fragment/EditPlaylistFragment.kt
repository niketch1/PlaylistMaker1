package com.example.playlistmaker1.media.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker1.R
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.media.ui.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditPlaylistFragment: NewPlaylistFragment() {

    override val viewModel by viewModel<EditPlaylistViewModel>()

    companion object {
        private const val PLAYLISTID = "playlistId"
        fun createArgs(playlistId: String): Bundle =
            bundleOf(
                PLAYLISTID to playlistId)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val convertedPlaylistId =  requireArguments().getString(PLAYLISTID)!!
            .toInt()!!

        binding.tvTitle.text = resources.getString(R.string.edit)
        binding.bCreatePlaylistButton.text = resources.getString(R.string.save)
        viewModel.getPlaylistById(convertedPlaylistId)

        viewModel.observePlaylist().observe(viewLifecycleOwner) {
            if(it !== null) {
                currentPlaylist = it
                binding.etNewPlaylistNameInput.setText(it.playlistName)
                binding.etDescriptionInput.setText(it.playlistDescription)
                binding.ivNewPlaylistImage.background = null

                Glide.with(this)
                    .load(it.imageFilePath)
                    .centerCrop()
                    .placeholder(R.drawable.icon_placeholder)
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
                    .into(binding.ivNewPlaylistImage)
            }else findNavController().popBackStack()
        }

        binding.llButtonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.bCreatePlaylistButton.setOnClickListener {
            createPlaylist(currentPlaylist)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

    }

    override fun createPlaylist(playlist: Playlist){
        if(currentUri == null){
            imageFilePath = playlist.imageFilePath
        }else saveImageToPrivateStorage(currentUri)
        viewModel.addPlaylist(playlist.copy(
            playlistName = binding.etNewPlaylistNameInput.text.toString(),
            playlistDescription = binding.etDescriptionInput.text.toString(),
            imageFilePath = imageFilePath ?: setPlaceholder(),
        ) )
    }
}

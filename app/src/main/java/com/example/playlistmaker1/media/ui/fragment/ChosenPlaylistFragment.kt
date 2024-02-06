package com.example.playlistmaker1.media.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.DateTimeUtil.showSumTracksTime
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentChosenPlaylistBinding
import com.example.playlistmaker1.media.ui.recycler_view.ChosenPlaylistTrackAdapter
import com.example.playlistmaker1.media.ui.view_model.ChosenPlaylistViewModel
import com.example.playlistmaker1.player.ui.fragment.AudioplayerFragment
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChosenPlaylistFragment: Fragment() {

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {
        private const val PLAYLISTID = "playlistId"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(
                PLAYLISTID to playlistId)
    }

    private val trackAdapter = ChosenPlaylistTrackAdapter({ track ->
        onTrackClickDebounce(track)
    },{ track ->
        chosenPlaylistViewModel.currentTrack = track
        showConfirmDialogDeleteTrack()
    })

    private val chosenPlaylistViewModel by viewModel<ChosenPlaylistViewModel>()
    private var _binding: FragmentChosenPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChosenPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(SearchFragment.CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { track ->
            findNavController().navigate(R.id.action_chosenPlaylistFragment_to_audioplayerFragment,
                AudioplayerFragment.createArgs(createJsonFromTrack(track)))
        }

        val convertedPlaylistId = requireArguments().getInt(PLAYLISTID)


        chosenPlaylistViewModel.getPlaylistById(convertedPlaylistId)

        chosenPlaylistViewModel.observeChosenPlaylist().observe(viewLifecycleOwner) {
            if(it !== null) {
                binding.tvPlaylistName.text = it.playlistName
                binding.tvDescription.text = it.playlistDescription
                binding.tvTrackNumbers.text = resources.getQuantityString(
                    R.plurals.plurals_track_1,
                    it.numberOfTracks,
                    it.numberOfTracks
                )
                binding.innerPlaylistItem.playlistName.text = it.playlistName
                binding.innerPlaylistItem.numberOfTracks.text = it.numberOfTracks.toString()
                binding.innerPlaylistItem.tracks.text =
                    this.resources.getQuantityString(R.plurals.plurals_track, it.numberOfTracks)
                showTracklist(it.numberOfTracks)
                Glide.with(this)
                    .load(it.imageFilePath)
                    .centerCrop()
                    .placeholder(R.drawable.icon_placeholder)
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
                    .into(binding.ivPlaylistImage)

                Glide.with(this)
                    .load(it.imageFilePath)
                    .centerCrop()
                    .placeholder(R.drawable.icon_placeholder)
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.playlist_icon_radius)))
                    .into(binding.innerPlaylistItem.playlistImage)
            }else  findNavController().popBackStack()
        }

        chosenPlaylistViewModel.observeTrackList().observe(viewLifecycleOwner) {
            binding.tvMinuteNumbers.text = showSumTracksTime(it, requireContext())
            trackAdapter.setTracks(it)
        }

        binding.rvBottomSheet.adapter = trackAdapter

        val bottomSheetMoreBehavior = BottomSheetBehavior.from(binding.llBottomSheetShare).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        val bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.llBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED

        }

        bottomSheetMoreBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.vOverlay.visibility = View.GONE
                        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    else -> {
                        binding.vOverlay.visibility = View.VISIBLE
                        bottomSheetTracksBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.ibButtonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.ibButtonShare.setOnClickListener {
            sharePlaylist()
        }

        binding.ibButtonMore.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.flbottomSheetShare.setOnClickListener{
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.flbottomSheetEdit.setOnClickListener{
            findNavController().navigate(R.id.action_chosenPlaylistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(chosenPlaylistViewModel.currentPlaylist.playlistId))
        }

        binding.flbottomSheetDelete.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showConfirmDialogDeletePlaylist()

        }
    }

    private fun showConfirmDialogDeletePlaylist(){
        val stringDelete = requireContext().resources.getString(R.string.want_to_delete_playlist)
        val confirmDialogDeletePlaylistBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle("$stringDelete \u00AB${chosenPlaylistViewModel.currentPlaylist.playlistName}\u00BB?")
            .setNegativeButton(R.string.no) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(R.string.yes) { dialog, which ->
                chosenPlaylistViewModel
                    .deletePlaylist()

            }
        confirmDialogDeletePlaylistBuilder.show()
    }

    private fun showConfirmDialogDeleteTrack(){
        val confirmDialogDeleteTrackBuilder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.want_to_delete_track)
            .setNegativeButton(R.string.no) { dialog, which ->
                // ничего не делаем
            }.setPositiveButton(R.string.yes) { dialog, which ->
                chosenPlaylistViewModel.deleteTrackFromPlaylist()
            }
        confirmDialogDeleteTrackBuilder.show()
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }



    private fun sharePlaylist(){
        if(chosenPlaylistViewModel.currentPlaylist.numberOfTracks == 0){
            Toast.makeText(requireContext(), R.string.toast_share_playlist, Toast.LENGTH_LONG).show()
        }
        else{
            chosenPlaylistViewModel
                .sharePlaylist(requireContext())
        }
    }

    private fun showTracklist(numberOfTracks : Int){
        if(numberOfTracks == 0){
            binding.rvBottomSheet.visibility = View.GONE
            binding.tvPlaceholderText.visibility = View.VISIBLE
        }else{
            binding.rvBottomSheet.visibility = View.VISIBLE
            binding.tvPlaceholderText.visibility = View.GONE
        }
    }

}
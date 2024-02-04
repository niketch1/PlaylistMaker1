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
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentChosenPlaylistBinding
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.media.ui.recycler_view.ChosenPlaylistTrackAdapter
import com.example.playlistmaker1.media.ui.view_model.ChosenPlaylistActivityViewModel
import com.example.playlistmaker1.media.ui.view_model.ChosenPlaylistViewModel
import com.example.playlistmaker1.player.ui.fragment.AudioplayerFragment
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ChosenPlaylistFragment: Fragment() {

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    companion object {
        private const val PLAYLISTID = "playlistId"
        fun createArgs(playlistId: String): Bundle =
            bundleOf(
                PLAYLISTID to playlistId)
    }

    private val trackAdapter = ChosenPlaylistTrackAdapter({ track ->
        onTrackClickDebounce(track)
    },{ track ->
        currentTrack = track
        confirmDialogDeleteTrack.show()
    })

    private val chosenPlaylistViewModel by viewModel<ChosenPlaylistViewModel>()
    private val chosenPlaylistActivityViewModel by activityViewModel<ChosenPlaylistActivityViewModel>()
    private var _binding: FragmentChosenPlaylistBinding? = null
    private val binding get() = _binding!!
    lateinit var confirmDialogDeleteTrack: MaterialAlertDialogBuilder
    lateinit var confirmDialogDeletePlaylist: MaterialAlertDialogBuilder
    private var currentTrack: Track? = null
    private var currentPlaylist = Playlist()
    private var currentTrackList = mutableListOf<Track>()

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

        confirmDialogDeleteTrack = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить трек?")
            .setNegativeButton("Нет") { dialog, which ->
                // ничего не делаем
            }.setPositiveButton("Да") { dialog, which ->
                chosenPlaylistViewModel.deleteTrackFromPlaylist(currentTrack!!.trackId,currentPlaylist)
            }

        confirmDialogDeletePlaylist = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист ${currentPlaylist.playlistName} ?")
            .setNegativeButton("Нет") { dialog, which ->
                // ничего не делаем
            }.setPositiveButton("Да") { dialog, which ->
                chosenPlaylistActivityViewModel.deletePlaylist(currentPlaylist)

            }

        val convertedPlaylistId =  requireArguments().getString(PLAYLISTID)!!
            .toInt()!!

        chosenPlaylistViewModel.getPlaylistById(convertedPlaylistId)

        chosenPlaylistActivityViewModel.observeChosenPlaylist().observe(viewLifecycleOwner) {
            if(it == null) findNavController().popBackStack()
        }

        chosenPlaylistViewModel.observeChosenPlaylist().observe(viewLifecycleOwner) {
            if(it !== null) {
                currentPlaylist = it
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
            binding.tvMinuteNumbers.text = showSumTracksTime(it)
            trackAdapter.setTracks(it)
            currentTrackList.clear()
            currentTrackList.addAll(it)
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
                EditPlaylistFragment.createArgs(currentPlaylist.playlistId.toString()))
        }

        binding.flbottomSheetDelete.setOnClickListener {
            bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            confirmDialogDeletePlaylist.show()

        }
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    private fun showSumTracksTime(trackList: List<Track>): String{
        val totalTimeLong = trackList.sumOf { it.trackTimeMillis }
        val totalTimeString = SimpleDateFormat("mm", Locale.getDefault()).format(totalTimeLong.toInt())
        return resources.getQuantityString(R.plurals.plurals_minute, totalTimeString.toInt(), totalTimeString)
    }

    private fun makeMessage(playlist: Playlist, trackList: List<Track>): String{
        return "${playlist.playlistName}\n" +
                "${playlist.playlistDescription}\n" +
                "${resources.getQuantityString(R.plurals.plurals_track_1, playlist.numberOfTracks, playlist.numberOfTracks)}\n" +
                trackList.joinToString(separator = "\n",){
                    "${trackList.indexOf(it)+1}.${it.artistName} - ${it.trackName}(${SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)})"
                }
    }

    private fun sharePlaylist(){
        if(currentPlaylist.numberOfTracks == 0){
            Toast.makeText(requireContext(), "В этом плейлисте нет списка треков, которым можно поделиться", Toast.LENGTH_LONG).show()
        }
        else{
            chosenPlaylistViewModel.sharePlaylist(makeMessage(currentPlaylist, currentTrackList))
        }
    }
}
package com.example.playlistmaker1.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker1.R
import com.example.playlistmaker1.creator.debounce
import com.example.playlistmaker1.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker1.media.domain.model.Playlist
import com.example.playlistmaker1.player.ui.DateFormatUtil
import com.example.playlistmaker1.player.ui.PlayStatus
import com.example.playlistmaker1.player.ui.recycler_view.PlaylistBottomSheetAdapter
import com.example.playlistmaker1.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.viewmodel.ext.android.viewModel


class AudioplayerFragment : Fragment(){

    private val itemType = object : TypeToken<Track>() {}.type
    companion object {
        private const val TRACK = "track"
        fun createArgs(track: String): Bundle =
            bundleOf(
                TRACK to track)
    }

    private val dateFormatUtil = DateFormatUtil()
    private lateinit var currentTrackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: FloatingActionButton
    private lateinit var addButton: FloatingActionButton
    private lateinit var newPlaylistBottomSheet: Button
    private lateinit var recyclerViewBottomSheet: RecyclerView
    private lateinit var buttonBack: ImageButton
    private lateinit var bottomSheet: LinearLayout
    private lateinit var overlay: View
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var album: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit


    private val audioplayerViewModel by viewModel<AudioPlayerViewModel>()
    private var currentTrack: Track? = null
    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private val playlistBottomSheetAdapter = PlaylistBottomSheetAdapter {
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var convertedTrack = createTrackFromJson(requireArguments().getString(TRACK))

        onPlaylistClickDebounce = debounce<Playlist>(SearchFragment.CLICK_DEBOUNCE_DELAY_MILLIS, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            addToPlaylist(playlist)
        }

        currentTrackTime = binding.currentTrackTime
        playButton = binding.playButton
        likeButton = binding.likeButton
        addButton = binding.addButton
        newPlaylistBottomSheet = binding.newPlaylistBottomSheet
        recyclerViewBottomSheet = binding.recyclerViewBottomSheet
        trackImage = binding.trackImage
        trackAlbum = binding.trackAlbum
        trackCountry = binding.trackCountry
        trackName = binding.trackName
        trackGenre = binding.trackGenre
        trackTime = binding.trackTime
        trackYear = binding.trackYear
        album = binding.album
        artistName = binding.artistName
        buttonBack = binding.buttonBack
        bottomSheet = binding.bottomSheet
        overlay = binding.overlay

        currentTrackTime.text = getString(R.string.start_time)
        recyclerViewBottomSheet.adapter = playlistBottomSheetAdapter

        buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        Glide.with(this)
            .load(convertedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.icon_placeholder)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
            .into(trackImage)

        audioplayerViewModel.preparePlayer(convertedTrack)
        currentTrack = convertedTrack
        trackName.text = convertedTrack.trackName
        artistName.text = convertedTrack.artistName
        trackTime.text = dateFormatUtil.convertLongTimeToString(convertedTrack.trackTimeMillis)
        trackAlbum.text = convertedTrack.collectionName
        trackAlbum.visibility = View.VISIBLE
        album.visibility = View.VISIBLE
        trackYear?.text = dateFormatUtil.convertYearToString(convertedTrack.releaseDate)
        trackGenre.text = convertedTrack.primaryGenreName
        trackCountry.text = convertedTrack.country

        audioplayerViewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            changeButtonStyle(playStatus)
            changeLikeButtonStyle(playStatus)
            currentTrackTime.text = playStatus.progress
        }

        audioplayerViewModel.observePlaylists().observe(viewLifecycleOwner) {
            showContent(it)
        }
        audioplayerViewModel.observeAddedTrack().observe(viewLifecycleOwner){
            if(!it.first) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Toast.makeText(requireContext(), it.second, Toast.LENGTH_LONG).show()
        }

        playButton.setOnClickListener {
            audioplayerViewModel.playbackControl()
        }
        likeButton.setOnClickListener{
            val changedTrack = audioplayerViewModel.onFavoriteClicked(convertedTrack)
            convertedTrack = changedTrack
        }
        addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            audioplayerViewModel.fillData()
        }
        newPlaylistBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioplayerFragment_to_newPlaylistFragment)
        }
    }

    private fun addToPlaylist(playlist: Playlist){
        return audioplayerViewModel.addToPlaylist(currentTrack!!,playlist)
    }

    private fun showContent(playlists: List<Playlist>){
        playlistBottomSheetAdapter.setPlaylists(playlists)
    }

    private fun changeButtonStyle(playStatus: PlayStatus) {
        if(playStatus.completed){
            playButton.setImageResource(R.drawable.play)
            currentTrackTime.text = getString(R.string.start_time)
        }
        if(playStatus.isPlaying){
            playButton.setImageResource(R.drawable.pause)
        }
        else{
            playButton.setImageResource(R.drawable.play)
        }
    }

    private fun changeLikeButtonStyle(playStatus: PlayStatus){
        if(playStatus.isFavorite) {
            likeButton.setImageResource(R.drawable.liked_track)
        }
        else{
            likeButton.setImageResource(R.drawable.like)
        }
    }

    private fun createTrackFromJson(json: String?): Track{
        return Gson().fromJson(json, itemType)
    }

    override fun onPause() {
        super.onPause()
        audioplayerViewModel.pausePlayer()
    }

    override fun onResume() {
        super.onResume()
        audioplayerViewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioplayerViewModel.stopPlayer()
        _binding = null
    }
}
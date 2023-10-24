package com.example.playlistmaker1.player.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker1.R
import com.example.playlistmaker1.player.ui.DateFormatUtil
import com.example.playlistmaker1.player.ui.PlayStatus
import com.example.playlistmaker1.player.ui.view_model.AudioPlayerViewModel
import com.example.playlistmaker1.search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel


class AudioplayerActivity : AppCompatActivity() {

    private val dateFormatUtil = DateFormatUtil()
    private lateinit var currentTrackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private val viewModel: AudioPlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val back = findViewById<ImageButton>(R.id.buttonBack)

        back.setOnClickListener {
            finish()
        }

        val trackImage = findViewById<ImageView>(R.id.trackImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val trackAlbum = findViewById<TextView>(R.id.trackAlbum)
        val album = findViewById<TextView>(R.id.album)
        val trackYear = findViewById<TextView>(R.id.trackYear)
        val trackGenre = findViewById<TextView>(R.id.trackGenre)
        val trackCountry = findViewById<TextView>(R.id.trackCountry)
        currentTrackTime = findViewById(R.id.currentTrackTime)
        currentTrackTime.text = getString(R.string.start_time)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)

        val convertedTrack = intent.getSerializableExtra("TRACK") as Track
        Glide.with(this)
            .load(convertedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.icon_placeholder)
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
            .into(trackImage)

        viewModel.preparePlayer(convertedTrack)
        trackName.text = convertedTrack.trackName
        artistName.text = convertedTrack.artistName
        trackTime.text = dateFormatUtil.convertLongTimeToString(convertedTrack.trackTimeMillis)
        trackAlbum.text = convertedTrack.collectionName
        trackAlbum.visibility = View.VISIBLE
        album.visibility = View.VISIBLE
        trackYear?.text = dateFormatUtil.convertYearToString(convertedTrack.releaseDate)
        trackGenre.text = convertedTrack.primaryGenreName
        trackCountry.text = convertedTrack.country

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            changeButtonStyle(playStatus)
            currentTrackTime.text = playStatus.progress
        }

        playButton.setOnClickListener {
            viewModel.playbackControl()
        }
        pauseButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun changeButtonStyle(playStatus: PlayStatus) {
        if(playStatus.completed){
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            currentTrackTime.text = getString(R.string.start_time)
        }
        if(playStatus.isPlaying){
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }
        else{
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }
}
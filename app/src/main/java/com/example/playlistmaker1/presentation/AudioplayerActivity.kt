package com.example.playlistmaker1.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker1.Creator
import com.example.playlistmaker1.PLAYLIST_PREFERENCES
import com.example.playlistmaker1.R
import com.example.playlistmaker1.SearchActivity
import com.example.playlistmaker1.domain.models.AudioplayerState
import com.example.playlistmaker1.presentation.mapper.DateFormatUtil
import com.example.playlistmaker1.presentation.mapper.TrackFromJson


class AudioplayerActivity : AppCompatActivity() {

    private val dateFormatUtil = DateFormatUtil()
    private val trackFromJson = TrackFromJson()
    private var url : String? = null
    private lateinit var currentTrackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private var mainThreadHandler: Handler? = null
    private val audioplayerInteractor = Creator.provideAudioplayerInteractor()
    var audioplayerState = AudioplayerState.STATE_DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        mainThreadHandler = Handler(Looper.getMainLooper())

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

        val sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)
        val pressedTrack = sharedPreferences?.getString(SearchActivity.NEW_TRACK_KEY, null)
        if (pressedTrack != null){
            val convertedTrack = trackFromJson.createTrackFromJson(pressedTrack)
            Glide.with(this)
                .load(convertedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .centerCrop()
                .placeholder(R.drawable.icon_placeholder)
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
                .into(trackImage)

            url = convertedTrack.previewUrl
            trackName.text = convertedTrack.trackName
            artistName.text = convertedTrack.artistName
            trackTime.text = dateFormatUtil.convertLongTimeToString(convertedTrack.trackTimeMillis)
            trackAlbum.text = convertedTrack.collectionName
            trackAlbum.visibility = View.VISIBLE
            album.visibility = View.VISIBLE
            trackYear?.text = dateFormatUtil.convertYearToString(convertedTrack.releaseDate)
            trackGenre.text = convertedTrack.primaryGenreName
            trackCountry.text = convertedTrack.country

            audioplayerInteractor.preparePlayer(url!!,
                onPreparedCallback = {
                audioplayerState = AudioplayerState.STATE_PREPARED
                },
                onCompletionCallback = {
                    mainThreadHandler?.removeCallbacks(getCurrentTime())
                    audioplayerState = AudioplayerState.STATE_PREPARED
                    pauseButton.visibility = View.GONE;
                    playButton.visibility = View.VISIBLE;
                    currentTrackTime.text = getString(R.string.start_time)
                }
            )

            playButton.setOnClickListener {
                playbackControl()
            }
            pauseButton.setOnClickListener {
                playbackControl()
            }
        }
    }
    private fun getCurrentTime(): Runnable {
        return object : Runnable {
            override fun run() {
                if (audioplayerState == AudioplayerState.STATE_PLAYING) {
                    currentTrackTime.text = audioplayerInteractor.transferCurrentTime()
                    mainThreadHandler?.postDelayed(this, DELAY_MILLIS_Activity)
                }
            }
        }
    }

    private fun startPlayer() {
        audioplayerState = AudioplayerState.STATE_PLAYING
        mainThreadHandler?.post(getCurrentTime())
        audioplayerInteractor.startPlayer()
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
    }

    private fun pausePlayer() {
        if(audioplayerState == AudioplayerState.STATE_PLAYING) {
            audioplayerInteractor.pausePlayer()
        }
        audioplayerState = AudioplayerState.STATE_PAUSED
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        mainThreadHandler?.removeCallbacks(getCurrentTime())
    }

    private fun playbackControl() {
        when(audioplayerState) {
            AudioplayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            AudioplayerState.STATE_PREPARED, AudioplayerState.STATE_PAUSED -> {
                startPlayer()
            }
            AudioplayerState.STATE_DEFAULT -> {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mainThreadHandler?.removeCallbacks(getCurrentTime())
        mainThreadHandler = null
        audioplayerInteractor.onDestroy()
        super.onDestroy()
    }

    companion object {
        const val DELAY_MILLIS_Activity = 300L
    }
}
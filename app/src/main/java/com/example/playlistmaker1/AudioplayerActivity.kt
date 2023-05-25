package com.example.playlistmaker1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class AudioplayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private lateinit var url : String
    private lateinit var currentTrackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    val timer = getCurrentTime()
    private var mainThreadHandler: Handler? = null

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
        currentTrackTime.text = "00:00"
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        val sharedPreferences = getSharedPreferences(PLAYLIST_PREFERENCES, MODE_PRIVATE)

        val pressedTrack = sharedPreferences?.getString(SearchActivity.NEW_TRACK_KEY, null)
        if (pressedTrack != null){
            val convertedTrack = createTrackFromJson(pressedTrack)
            Glide.with(this)
                .load(convertedTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                .centerCrop()
                .placeholder(R.drawable.icon_placeholder)
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.track_image_radius)))
                .into(trackImage)

            url = convertedTrack.previewUrl
            trackName.text = convertedTrack.trackName
            artistName.text = convertedTrack.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(convertedTrack.trackTimeMillis)
            trackAlbum.text = convertedTrack.collectionName
            trackAlbum?.visibility = View.VISIBLE
            album?.visibility = View.VISIBLE
            val date = SimpleDateFormat("yyyy", Locale.getDefault()).parse(convertedTrack.releaseDate)
            trackYear?.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(date as Date)
            trackGenre.text = convertedTrack.primaryGenreName
            trackCountry.text = convertedTrack.country

            preparePlayer()
            playButton.setOnClickListener {
                playbackControl()
            }
            pauseButton.setOnClickListener {
                playbackControl()
            }
        }
    }
    private fun getCurrentTime() : Runnable {
        return object : Runnable {
            override fun run() {
                currentTrackTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)
                mainThreadHandler?.postDelayed(this, DELAY)
            }
        }
    }

    private fun createTrackFromJson(json: String?): Track{
        return Gson().fromJson(json, Track::class.java)
    }

    private var playerState = STATE_DEFAULT

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            mainThreadHandler?.removeCallbacks(timer)
            playerState = STATE_PREPARED
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            currentTrackTime.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        playerState = STATE_PLAYING
        mainThreadHandler?.post(timer)
    }

    private fun pausePlayer() {
        mainThreadHandler?.removeCallbacks(timer)
        mediaPlayer.pause()
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(timer)
    }
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }
}
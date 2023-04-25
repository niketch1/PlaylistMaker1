package com.example.playlistmaker1

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale



class AudioplayerActivity : AppCompatActivity() {
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
        val currentTrackTime = findViewById<TextView>(R.id.currentTrackTime)
        currentTrackTime.text = "0:00"
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

            trackName.text = convertedTrack.trackName
            artistName.text = convertedTrack.artistName
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(convertedTrack.trackTimeMillis)
            trackAlbum.text = convertedTrack.collectionName
            if(convertedTrack.collectionName == null){
                trackAlbum.visibility = View.GONE
                album.visibility = View.GONE
            }
            else{
                trackAlbum.visibility = View.VISIBLE
                album.visibility = View.VISIBLE
            }
            trackYear.text = convertedTrack.releaseDate?.take(4)
            trackGenre.text = convertedTrack.primaryGenreName
            trackCountry.text = convertedTrack.country
        }
    }

    private fun createTrackFromJson(json: String?): Track{
        return Gson().fromJson(json, Track::class.java)
    }

}
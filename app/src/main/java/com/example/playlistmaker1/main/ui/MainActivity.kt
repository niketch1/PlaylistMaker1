package com.example.playlistmaker1.main.ui
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.playlistmaker1.R
import com.example.playlistmaker1.media.ui.activity.MediaActivity
import com.example.playlistmaker1.search.ui.activity.SearchActivity
import com.example.playlistmaker1.settings.ui.activity.SettingsActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.buttonSearch)

        search.setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }

        val media = findViewById<Button>(R.id.buttonMedia)

        media.setOnClickListener {
            navigateTo(MediaActivity::class.java)
        }

        val settings = findViewById<Button>(R.id.buttonSettings)

        settings.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
    }
    private fun navigateTo(clazz: Class<out AppCompatActivity>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

}
package com.example.playlistmaker1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


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
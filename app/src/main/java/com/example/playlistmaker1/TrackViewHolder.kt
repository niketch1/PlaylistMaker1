package com.example.playlistmaker1

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(item: Track) {

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.sad)
            .transform(RoundedCorners(2))
            .into(trackImage)

        val name = item.trackName

        if(name.count() >= 30) {
           val name1 = name.dropLast(name.count() - 30).padEnd(33, '.')
           trackName.text = name1
        }
        else {
           trackName.text = name
        }
        artistName.text = item.artistName
        trackTime.text = item.trackTime
    }
}

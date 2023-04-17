package com.example.playlistmaker1


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SearchedTrackAdapter : RecyclerView.Adapter<TrackViewHolder> () {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}


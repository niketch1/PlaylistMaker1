package com.example.playlistmaker1


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker1.data.dto.TrackDto
import com.example.playlistmaker1.domain.models.Track
import com.google.gson.Gson

class SearchedTrackAdapter(val clickListener: TrackAdapter.TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    private val tracks = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { clickListener.onTrackClick(tracks.get(position))}
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun setTracks(newTracks: List<Track>?) {
        tracks.clear()
        if (!newTracks.isNullOrEmpty()) {
            tracks.addAll(newTracks)
        }
        notifyDataSetChanged()
    }

    fun addTrack(newTrack: Track) {
        var listHasBeenUpdated = false
        if (newTrack !== null) {
            for (i in tracks.indices) {
                if (tracks[i].trackId == newTrack.trackId) {
                    tracks.add(0, newTrack)
                    tracks.removeAt(i+1)
                    listHasBeenUpdated = true
                }
            }
            if (!listHasBeenUpdated) {
                tracks.add(0, newTrack)
            }
            if(tracks.size > SearchActivity.SEARCHED_TRACK_SIZE) {
                tracks.removeAt(tracks.size-1)
            }
            notifyDataSetChanged()
        }
    }

    fun createJsonFromTrackList(): String {
        return Gson().toJson(tracks)
    }

    fun isSearchedTrackListEmpty() : Boolean{
        return tracks.isNullOrEmpty()
    }
}


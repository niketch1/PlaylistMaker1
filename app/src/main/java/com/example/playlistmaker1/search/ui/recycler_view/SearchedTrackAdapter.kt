package com.example.playlistmaker1.search.ui.recycler_view


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker1.search.domain.model.Track
import com.example.playlistmaker1.search.ui.fragment.SearchFragment

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

    fun getCurrentTrackList() : MutableList<Track>{
        return tracks
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
            if(tracks.size > SearchFragment.SEARCHED_TRACK_SIZE) {
                tracks.removeAt(tracks.size-1)
            }
            notifyDataSetChanged()
        }
    }

    fun isSearchedTrackListEmpty() : Boolean{
        return tracks.isNullOrEmpty()
    }
}


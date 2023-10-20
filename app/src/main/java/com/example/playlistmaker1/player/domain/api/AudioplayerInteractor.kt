package com.example.playlistmaker1.player.domain.api

interface AudioplayerInteractor {

    fun preparePlayer(url : String, onPreparedCallback: () -> Unit, onCompletionCallback: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun transferCurrentTime() : String
    fun onDestroy()
}
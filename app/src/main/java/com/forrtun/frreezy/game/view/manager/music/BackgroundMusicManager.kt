package com.forrtun.frreezy.game.view.manager.music

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.collections.set

class BackgroundMusicManager(mediaPlayerFactory: MediaPlayerFactory) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundUriMap: MutableMap<String, Uri> = mutableMapOf()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        mediaPlayer = mediaPlayerFactory.create()
    }

    fun loadBackgroundMusic(name: String, uri: Uri) {
        soundUriMap[name] = uri
    }

    fun startMusic(context: Context, soundName: String, loop: Boolean = false) {
        val uri = soundUriMap[soundName]
        if (uri != null) {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(context, uri)
            mediaPlayer?.prepare()
            mediaPlayer?.isLooping = loop
            scope.launch {
                mediaPlayer?.start()
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun resumeMusic() {
        mediaPlayer?.start()
    }

    fun releaseMusicPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        scope.cancel()
    }
}
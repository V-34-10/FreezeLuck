package com.forrtun.frreezy.game.view.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BackgroundMusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundIdMap: MutableMap<String, Int> = mutableMapOf()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        initMusicPlayer()
    }

    private fun initMusicPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }

    fun loadBackgroundMusic(context: Context, name: String, res: Int) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(context, res)
        soundIdMap[name] = res
    }

    fun startMusic(soundName: String, loop: Boolean = false) {
        val resId = soundIdMap[soundName]
        if (resId != null) {
            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = loop
            scope.launch {
                mediaPlayer?.start()
            }
        }
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
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
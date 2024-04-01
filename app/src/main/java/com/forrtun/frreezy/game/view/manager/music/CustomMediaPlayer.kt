package com.forrtun.frreezy.game.view.manager.music

import android.media.AudioAttributes
import android.media.MediaPlayer

class CustomMediaPlayer : MediaPlayerFactory {
    override fun create(): MediaPlayer {
        return MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }
}
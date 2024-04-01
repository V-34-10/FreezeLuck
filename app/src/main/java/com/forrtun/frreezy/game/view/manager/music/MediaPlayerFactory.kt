package com.forrtun.frreezy.game.view.manager.music

import android.media.MediaPlayer

interface MediaPlayerFactory {
    fun create(): MediaPlayer
}
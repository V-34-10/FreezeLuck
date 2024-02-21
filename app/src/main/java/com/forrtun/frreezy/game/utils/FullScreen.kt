package com.forrtun.frreezy.game.utils

import android.app.Activity
import android.view.View

object FullScreen {
    fun setFullScreen(activity: Activity) {
        val view = activity.window.decorView
        view.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
    }
}
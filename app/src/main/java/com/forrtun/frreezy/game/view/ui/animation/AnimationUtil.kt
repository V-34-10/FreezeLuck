package com.forrtun.frreezy.game.view.ui.animation

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.forrtun.frreezy.game.R

object AnimationUtil {
    fun loadButtonAnimation(context: Context): Animation {
        return AnimationUtils.loadAnimation(context, R.anim.button_animation)
    }
}
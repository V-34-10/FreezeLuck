package com.forrtun.frreezy.game.view.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.forrtun.frreezy.game.R

object StatusDialog {
    fun runDialog(score: Int, activity: Activity, layoutId: Int) {
        val animationClick = AnimationUtils.loadAnimation(activity, R.anim.button_animation)
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutId)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)

        val textScore = dialog.findViewById<TextView>(R.id.text_score)
        textScore.text = score.toString()

        val btnRestart = dialog.findViewById<ImageView>(R.id.btn_restart)
        btnRestart.setOnClickListener {
            it.startAnimation(animationClick)
            dialog.dismiss()
        }

        val btnContinue = dialog.findViewById<ImageView>(R.id.btn_continue)
        btnContinue.setOnClickListener {
            it.startAnimation(animationClick)
            dialog.dismiss()
            activity.onBackPressed()
        }

        dialog.show()
    }
}
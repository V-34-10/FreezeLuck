package com.forrtun.frreezy.game.view.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStake
import java.util.Random

object WheelHelper {
    private var currentAngle = 0f
    fun animRotateWheel(
        binding: GameBinding,
        activity: Activity,
        maxAngleRotate: Float,
        minAngleRotate: Float
    ) {
        val randomAngleRotate =
            minAngleRotate + Random().nextFloat() * (maxAngleRotate - minAngleRotate)
        val rotationAnimation = RotateAnimation(
            currentAngle,
            randomAngleRotate,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            fillAfter = true
            setAnimationListener(null)
        }
        rotationAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                currentAngle += randomAngleRotate
                updateStatusBalance(randomAngleRotate, binding, activity)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.wheel.startAnimation(rotationAnimation)
    }

    private fun convertAngleToDegree(angleRotate: Float): Float {
        val degree = angleRotate % 360
        return if (degree < 0) degree + 360 else degree
    }

    private fun getWinCoefficient(angleRotate: Float): Int {
        return when (convertAngleToDegree(angleRotate)) {
            in 0f..36f -> 2 // 2x
            in 36f..72f -> 3 // 3x
            in 72f..108f -> 1 // 1x
            in 108f..144f -> 2 // 2x
            in 144f..180f -> 2 // 2x
            in 180f..216f -> 4 // 4x
            in 216f..252f -> 0 // 0x
            in 252f..288f -> 1 // 1x
            in 288f..324f -> 0 // 0x
            in 324f..360f -> 4 // 4x
            else -> {
                return 1
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatusBalance(angle: Float, binding: GameBinding, activity: Activity) {
        val bid = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        if (getWinCoefficient(angle) == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance.text = "Total\n$balance"
        } else {
            val newSumWin = bid * getWinCoefficient(angle)
            balance += newSumWin
            binding.textBalance.text = "Total\n$balance"
            win += newSumWin
            binding.textWin.text = "WIN\n$win"
        }
        setStatusStake(
            activity,
            "Total\n$balance",
            bid.toString(),
            "WIN\n$win"
        )
    }
}
package com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers

import android.annotation.SuppressLint
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStake
import java.util.Random
import kotlin.math.roundToInt

object WheelHelper {
    private var currentAngle = 0f
    fun animRotateWheel(
        binding: GameWheelBinding,
        maxAngleRotate: Float,
        minAngleRotate: Float,
        onAnimationEnd: () -> Unit = {}
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
                updateStatusBalance(randomAngleRotate, binding)

                onAnimationEnd()
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

    private fun getWinSecondCoefficient(angleRotate: Float): Float {
        return when (convertAngleToDegree(angleRotate)) {
            in 0f..36f -> 1.5f // 1.5x
            in 36f..72f -> 1.0f // 1x
            in 72f..108f -> 3.0f // 3x
            in 108f..144f -> 5.0f // 5x
            in 144f..180f -> 1.5f // 1.5x
            in 180f..216f -> 0.0f // 0x
            in 216f..252f -> 1.5f // 1.5x
            in 252f..288f -> 5.0f // 5x
            in 288f..324f -> 3.0f // 3x
            in 324f..360f -> 1.0f // 1x
            else -> {
                return 1.0f
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatusBalance(angle: Float, binding: GameWheelBinding) {
        val bet = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        val coefficient = when (binding) {
            is FragmentWheelFourGameBindingImpl -> getWinSecondCoefficient(angle)
            is FragmentWheelThreeGameBindingImpl -> getWinCoefficient(angle)
            else -> {
                0.0f
            }
        }

        if (coefficient.toInt() == 0) {
            balance -= bet
            win -= bet
            if (balance < 0) balance = 0
            if (win < 0) win = 0
            binding.textBalance.text = "Total\n$balance"
            binding.textWin.text = "WIN\n$win"
        } else {
            val newSumWin = (bet.toFloat() * coefficient.toFloat()).roundToInt()
            balance += newSumWin
            binding.textBalance.text = "Total\n$balance"
            win += newSumWin
            binding.textWin.text = "WIN\n$win"
        }
        setStatusStake(
            "Total\n$balance",
            bet.toString(),
            "WIN\n$win"
        )
    }
}
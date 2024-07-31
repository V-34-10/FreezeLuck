package com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStake
import java.util.Random
import kotlin.math.roundToInt

data class SectorResult(val coefficient: Float, val centralAngle: Float)

object WheelHelper {
    private var currentAngle = 0f
    fun animRotateWheel(
        binding: GameWheelBinding,
        onAnimationEnd: () -> Unit = {}
    ) {
        val randomTargetAngle = getRandomAngle(binding)
        val targetAngle = currentAngle + 360f + randomTargetAngle
        val rotationAnimation = RotateAnimation(
            currentAngle,
            targetAngle,
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
                currentAngle = (targetAngle) % 360f

                val sectorResult = getWinCoefficientInSector(currentAngle, binding)
                updateStatusBalance(sectorResult.coefficient, binding)

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

    private fun getRandomAngle(binding: GameWheelBinding): Float {
        return when (binding) {
            is FragmentWheelThreeGameBindingImpl -> {
                when (Random().nextInt(8)) {
                    0 -> 0f
                    1 -> 45f
                    2 -> 90f
                    3 -> 135f
                    4 -> 180f
                    5 -> 225f
                    6 -> 270f
                    7 -> 315f
                    else -> 0f
                }
            }
            is FragmentWheelFourGameBindingImpl -> {
                when (Random().nextInt(10)) {
                    0 -> 15f
                    1 -> 50f
                    2 -> 90f
                    3 -> 125f
                    4 -> 165f
                    5 -> 195f
                    6 -> 235f
                    7 -> 270f
                    8 -> 305f
                    9 -> 345f
                    else -> 0f
                }
            }
            else -> 0f
        }
    }

    private fun getWinCoefficientInSector(angleRotate: Float, binding: GameWheelBinding): SectorResult {
        return when (binding) {
            is FragmentWheelThreeGameBindingImpl -> {
                when (convertAngleToDegree(angleRotate)) {
                    in 0f..20f, in 335f..360f -> SectorResult(4.0f, 0f) // 4x,
                    in 21f..64f -> SectorResult(2.0f, 45f) // 2x, кут 45
                    in 65f..109f -> SectorResult(3.0f, 90f) // 3x, кут 90
                    in 110f..154f -> SectorResult(2.0f, 125f) // 2x, кут 135
                    in 155f..199f -> SectorResult(2.0f, 180f) // 2x, кут 180
                    in 200f..239f -> SectorResult(4.0f, 220f) // 4x, кут 225
                    in 244f..284f -> SectorResult(0.0f, 260f) // 0x, кут 270
                    in 285f..330f -> SectorResult(0.0f, 310f) // 0x, кут 315
                    else -> {
                        SectorResult(0.0f, 0f)
                    }
                }
            }

            is FragmentWheelFourGameBindingImpl -> {
                when (convertAngleToDegree(angleRotate)) {
                    in 0f..35f -> SectorResult(1.5f, 15f)  // 1.5x
                    in 36f..73f -> SectorResult(1.0f, 50f) // 1x
                    in 74f..108f -> SectorResult(3.0f, 90f) // 3x
                    in 109f..144f -> SectorResult(5.0f, 125f) // 5x
                    in 145f..181f -> SectorResult(1.5f, 165f) // 1.5x
                    in 182f..217f -> SectorResult(0.0f, 195f)  // 0x
                    in 218f..253f -> SectorResult(1.5f, 235f) // 1.5x
                    in 254f..284f -> SectorResult(5.0f, 270f) // 5x
                    in 285f..323f -> SectorResult(3.0f, 305f) // 3x
                    in 324f..360f -> SectorResult(1.0f, 345f) // 1x
                    else -> {
                        SectorResult(0.0f, 0f)
                    }
                }
            }

            else ->  SectorResult(0.0f, 0f)

        }
    }

    private fun setWinStatus(binding: GameWheelBinding, coefficient: Float) {
        when (binding) {
            is FragmentWheelThreeGameBindingImpl -> {
                binding.winCoeff.visibility = View.VISIBLE
                val imageResource = when (coefficient) {
                    4.0f -> R.drawable.circle_win_1
                    3.0f -> R.drawable.circle_win_2
                    0.0f -> R.drawable.circle_win_3
                    2.0f -> R.drawable.circle_win_4
                    else -> R.drawable.circle_win_default
                }
                binding.winCoeff.setImageResource(imageResource)
            }
            is FragmentWheelFourGameBindingImpl -> {
                binding.winCoeff.visibility = View.VISIBLE
                val imageResource = when (coefficient) {
                    1.0f -> R.drawable.circle_win_8
                    3.0f -> R.drawable.circle_win_2
                    0.0f -> R.drawable.circle_win_3
                    1.5f -> R.drawable.circle_win_9
                    5.0f -> R.drawable.circle_win_10
                    else -> R.drawable.circle_win_default
                }
                binding.winCoeff.setImageResource(imageResource)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatusBalance(angle: Float, binding: GameWheelBinding) {
        val bet = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        setWinStatus(binding, angle)

        if (angle.toInt() == 0) {
            balance -= bet
            win -= bet
            if (balance < 0) balance = 0
            if (win < 0) win = 0
            binding.textBalance.text = "Total\n$balance"
            binding.textWin.text = "WIN\n$win"
        } else {
            val newSumWin = (bet.toFloat() * angle.toFloat()).roundToInt()
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
package com.forrtun.frreezy.game.view.ui.scene.games

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStake
import java.util.Random

class WheelThreeGameFragment : Fragment() {

    private lateinit var binding: FragmentWheelThreeGameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelThreeGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControlButton()
        activity?.let { context ->
            if (isTotalSave(context)) {
                val (saveTotal) = getStatusStake(context)
                val total = convertStringToNumber(saveTotal.toString())
                binding.textBalance.text = "Total\n$total"
            }
        }
    }

    private fun initControlButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
        binding.btnSpin.setOnClickListener {
            it!!.startAnimation(animation)
            if (convertStringToNumber(getString(R.string.default_total_balance)) <= 0) {
                return@setOnClickListener
            }
            animRotateWheel()
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            activity?.onBackPressed()
        }
    }

    private fun animRotateWheel() {
        val minAngleRotate = 0f
        val maxAngleRotate = 720f
        var currentAngle = 0f
        val randomAngleRotate =
            minAngleRotate + Random().nextFloat() * (maxAngleRotate - minAngleRotate)
        val rotationAnimation = RotateAnimation(
            0f,
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
                updateStatusBalance(randomAngleRotate)
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
    private fun updateStatusBalance(angle: Float) {
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
        activity?.let { it1 ->
            setStatusStake(
                it1,
                "Total\n$balance",
                bid.toString(),
                "WIN\n$win"
            )
        }
    }
}
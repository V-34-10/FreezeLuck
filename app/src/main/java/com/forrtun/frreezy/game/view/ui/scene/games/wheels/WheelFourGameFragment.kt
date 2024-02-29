package com.forrtun.frreezy.game.view.ui.scene.games.wheels

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentWheelFourGameBinding
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.FragmentWheelFourGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.WheelHelper

class WheelFourGameFragment : Fragment() {
    private lateinit var binding: FragmentWheelFourGameBinding
    private val minAngleRotate = 10f
    private val maxAngleRotate = 720f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelFourGameBinding.inflate(layoutInflater, container, false)
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

    @SuppressLint("SetTextI18n")
    private fun initControlButton() {
        val buttonListBet = listOf(
            binding.btnSetStakeFirst to 50,
            binding.btnSetStakeSecond to 100,
            binding.btnSetStakeThree to 150,
            binding.btnSetStakeFour to 200,
            binding.btnSetStakeFife to 250,
            binding.btnSetStakeSix to 300
        )
        var animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
        binding.btnSpin.setOnClickListener {
            it!!.startAnimation(animation)
            if (convertStringToNumber(getString(R.string.default_total_balance)) <= 0) {
                return@setOnClickListener
            }
            activity?.let { it1 ->
                WheelHelper.animRotateWheel(
                    FragmentWheelFourGameBindingImpl(binding),
                    it1,
                    maxAngleRotate,
                    minAngleRotate
                )
            }
        }
        buttonListBet.forEach { (button, betValue) ->
            button.setOnClickListener {
                animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
                button.startAnimation(animation)
                binding.textBet.text = "BET\n$betValue"
            }
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            if (convertStringToNumber(binding.textWin.text.toString()) == 0) {
                activity?.let { it1 ->
                    runDialog(
                        convertStringToNumber(binding.textWin.text.toString()),
                        it1, R.layout.dialog_lose
                    )
                }
            } else {
                activity?.let { it1 ->
                    runDialog(
                        convertStringToNumber(binding.textWin.text.toString()),
                        it1, R.layout.dialog_win
                    )
                }
            }
        }
    }
}
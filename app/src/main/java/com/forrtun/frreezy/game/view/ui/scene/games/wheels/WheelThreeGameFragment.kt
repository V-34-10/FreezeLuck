package com.forrtun.frreezy.game.view.ui.scene.games.wheels

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.FragmentWheelThreeGameBindingImpl
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.WheelHelper

class WheelThreeGameFragment : Fragment() {

    private lateinit var binding: FragmentWheelThreeGameBinding
    private val minAngleRotate = 0f
    private val maxAngleRotate = 720f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelThreeGameBinding.inflate(layoutInflater, container, false)
        FragmentWheelThreeGameBindingImpl(binding)
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
            activity?.let { it1 ->
                WheelHelper.animRotateWheel(
                    FragmentWheelThreeGameBindingImpl(binding),
                    it1,
                    maxAngleRotate,
                    minAngleRotate
                )
            }
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            activity?.onBackPressed()
        }
    }
}
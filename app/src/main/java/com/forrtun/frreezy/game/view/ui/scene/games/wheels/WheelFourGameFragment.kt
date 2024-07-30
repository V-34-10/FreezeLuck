package com.forrtun.frreezy.game.view.ui.scene.games.wheels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentWheelFourGameBinding
import com.forrtun.frreezy.game.view.manager.music.BackgroundMusicManager
import com.forrtun.frreezy.game.view.manager.music.CustomMediaPlayer
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.ui.animation.AnimationUtil
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.FragmentWheelFourGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.WheelHelper

class WheelFourGameFragment : Fragment() {
    private lateinit var binding: FragmentWheelFourGameBinding
    private lateinit var backgroundMusic: BackgroundMusicManager
    private val minAngleRotate = 360f
    private val maxAngleRotate = 720f
    private var runGame = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelFourGameBinding.inflate(layoutInflater, container, false)
        initSoundPool()
        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        backgroundMusic.resumeMusic()
    }

    override fun onPause() {
        super.onPause()
        backgroundMusic.pauseMusic()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControlButton()
        loadStatusTotal()
    }

    @SuppressLint("SetTextI18n")
    private fun loadStatusTotal() {
        activity?.let { context ->
            if (isTotalSave()) {
                getStatusStake(context).let { (total, _, _) ->
                    total?.let {
                        binding.textBalance.text = "Total\n${convertStringToNumber(total)}"
                    }
                }
            }
        }
    }

    private fun initSoundPool() {
        backgroundMusic = BackgroundMusicManager(CustomMediaPlayer())
        backgroundMusic.loadBackgroundMusic(
            "backgroundMusic",
            Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.kazino_zvuk)
        )
        backgroundMusic.startMusic(requireContext(), "backgroundMusic", true)
    }

    @SuppressLint("SetTextI18n")
    private fun initControlButton() {
        binding.btnSpin.setOnClickListener { button ->
            button.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            button.isEnabled = false
            if (convertStringToNumber(getString(R.string.default_total_balance)) <= 0) {
                button.isEnabled = true
                return@setOnClickListener
            }
            binding.winCoeff.visibility = View.GONE
            activity?.let {
                WheelHelper.animRotateWheel(
                    FragmentWheelFourGameBindingImpl(binding),
                    maxAngleRotate,
                    minAngleRotate
                ) {
                    button.isEnabled = true
                    runGame = true
                }
            }
        }
        setupBetButtons(
            listOf(
                binding.btnSetStakeFirst,
                binding.btnSetStakeSecond,
                binding.btnSetStakeThree,
                binding.btnSetStakeFour,
                binding.btnSetStakeFife,
                binding.btnSetStakeSix
            ),
            listOf(50, 100, 150, 200, 250, 300)
        )
        binding.btnBack.setOnClickListener {
            it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            if (runGame) {
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
            } else {
                activity?.onBackPressed()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupBetButtons(buttons: List<ImageView>, betValues: List<Int>) {
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
                binding.textBet.text = "BET\n${betValues[index]}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
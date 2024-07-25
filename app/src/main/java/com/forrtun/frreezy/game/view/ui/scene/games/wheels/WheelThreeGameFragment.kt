package com.forrtun.frreezy.game.view.ui.scene.games.wheels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding
import com.forrtun.frreezy.game.view.manager.music.BackgroundMusicManager
import com.forrtun.frreezy.game.view.manager.music.CustomMediaPlayer
import com.forrtun.frreezy.game.view.manager.stake.ManagerStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStakeUI
import com.forrtun.frreezy.game.view.ui.animation.AnimationUtil
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.FragmentWheelThreeGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.wheels.helpers.WheelHelper

class WheelThreeGameFragment : Fragment() {

    private lateinit var binding: FragmentWheelThreeGameBinding
    private lateinit var managerStatusStake: ManagerStatusStake
    private lateinit var backgroundMusic: BackgroundMusicManager
    private val minAngleRotate = 10f
    private val maxAngleRotate = 720f
    private var runGame = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWheelThreeGameBinding.inflate(layoutInflater, container, false)
        managerStatusStake =
            UpdateStatusStake.constructor(
                requireContext(),
                convertStringToNumber(binding.textBalance.text.toString())
            )
        setStatusStakeUI(binding, managerStatusStake)
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

    private fun initControlButton() {
        binding.btnSpin.setOnClickListener {button ->
            button.isEnabled = false
            button.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            if (convertStringToNumber(getString(R.string.default_total_balance)) <= 0) {
                button.isEnabled = true
                return@setOnClickListener
            }
            activity?.let {
                WheelHelper.animRotateWheel(
                    FragmentWheelThreeGameBindingImpl(binding),
                    maxAngleRotate,
                    minAngleRotate
                ){
                    button.isEnabled = true
                    runGame = true
                }
            }
        }
        binding.btnMinus.setOnClickListener {
            it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            managerStatusStake.decreaseBet()
            setStatusStakeUI(binding, managerStatusStake)
        }
        binding.btnPlus.setOnClickListener {
            it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            managerStatusStake.increaseBet()
            setStatusStakeUI(binding, managerStatusStake)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
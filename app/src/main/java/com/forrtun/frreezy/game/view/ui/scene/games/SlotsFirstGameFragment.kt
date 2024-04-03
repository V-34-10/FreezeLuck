package com.forrtun.frreezy.game.view.ui.scene.games

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentSlotsFirstGameBinding
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.adapter.RecyclerSlotAdapter
import com.forrtun.frreezy.game.view.adapter.VerticalSpaceItemDecoration
import com.forrtun.frreezy.game.view.manager.music.BackgroundMusicManager
import com.forrtun.frreezy.game.view.manager.music.CustomMediaPlayer
import com.forrtun.frreezy.game.view.manager.stake.ManagerStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.constructor
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStakeUI
import com.forrtun.frreezy.game.view.ui.animation.AnimationUtil
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.FragmentSlotFirstGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.MinerHelper

class SlotsFirstGameFragment : Fragment() {

    private lateinit var binding: FragmentSlotsFirstGameBinding
    private lateinit var managerStatusStake: ManagerStatusStake
    private lateinit var backgroundMusic: BackgroundMusicManager
    private lateinit var slotAdapter: RecyclerSlotAdapter
    private var animationStart = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlotsFirstGameBinding.inflate(layoutInflater, container, false)
        managerStatusStake = constructor(
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
        val originalSlotMutableList = mutableListOf(
            R.drawable.ic_slot_6a,
            R.drawable.ic_slot_5a,
            R.drawable.ic_slot_1a,
            R.drawable.ic_slot_5a,
            R.drawable.ic_slot_2a,
            R.drawable.ic_slot_6a,
            R.drawable.ic_slot_4a,
            R.drawable.ic_slot_2a,
            R.drawable.ic_slot_5a,
            R.drawable.ic_slot_1a,
            R.drawable.ic_slot_6a,
            R.drawable.ic_slot_3a,
            R.drawable.ic_slot_6a,
            R.drawable.ic_slot_2a,
            R.drawable.ic_slot_5a,
            R.drawable.ic_slot_2a,
        )
        setSlotRecycler(originalSlotMutableList)
        initControlButton(originalSlotMutableList)
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

    private fun initControlButton(originalSlotMutableList: MutableList<Int>) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
        binding.btnSpin.setOnClickListener {
            it.startAnimation(animation)
            if (animationStart || convertStringToNumber(getString(R.string.default_total_balance)) <= 0) {
                return@setOnClickListener
            }

            binding.sceneSlots.clearAnimation()
            slotAdapter.resetSlotList(emptyList())

            animation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    animationStart = true
                }

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(animation: Animation?) {
                    animationStart = false
                    activity?.let {
                        MinerHelper.updateStatusBalance(
                            null,
                            FragmentSlotFirstGameBindingImpl(binding),
                            originalSlotMutableList.map { Slot(it) }
                        )
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            originalSlotMutableList.shuffle()
            slotAdapter.resetSlotList(originalSlotMutableList.map { Slot(it) })
            slotAdapter.setAnimationSpin()
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

    private fun setSlotRecycler(originalSlotMutableList: MutableList<Int>) {
        binding.sceneSlots.addItemDecoration(
            VerticalSpaceItemDecoration(
                resources.getDimensionPixelSize(
                    R.dimen.vertical_spacing
                )
            )
        )
        slotAdapter = RecyclerSlotAdapter(originalSlotMutableList.map { Slot(it) })
        binding.sceneSlots.layoutManager = GridLayoutManager(context, 4)
        binding.sceneSlots.adapter = slotAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
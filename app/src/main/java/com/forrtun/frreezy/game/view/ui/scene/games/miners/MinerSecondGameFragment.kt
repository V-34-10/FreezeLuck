package com.forrtun.frreezy.game.view.ui.scene.games.miners

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentMinerSecondGameBinding
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.adapter.RecyclerSlotMinerAdapter
import com.forrtun.frreezy.game.view.adapter.SlotClickListener
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
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.FragmentMinerSecondGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.MinerHelper

class MinerSecondGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentMinerSecondGameBinding
    private lateinit var managerStatusStake: ManagerStatusStake
    private lateinit var backgroundMusic: BackgroundMusicManager
    private lateinit var slotMinerAdapter: RecyclerSlotMinerAdapter
    private var runGame = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMinerSecondGameBinding.inflate(layoutInflater, container, false)
        managerStatusStake =
            constructor(
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
        val originalSlotMutableList = MutableList(12) { R.drawable.ic_slot_7b }
        setSlotRecycler(originalSlotMutableList)
        initControlButton(originalSlotMutableList)
        slotMinerAdapter.setMinerClickListener(this)
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
        binding.btnSpin.setOnClickListener { it ->
            it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
            if (convertStringToNumber(getString(R.string.default_total)) <= 0) {
                return@setOnClickListener
            }
            binding.sceneSlots.let {
                slotMinerAdapter.updateSlotList(originalSlotMutableList.map {
                    Slot(
                        it
                    )
                })
            }
            runGame = true
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

    private fun setSlotRecycler(originalSlotMutableList: MutableList<Int>) {
        slotMinerAdapter = RecyclerSlotMinerAdapter(originalSlotMutableList.map { Slot(it) }, 5)
        binding.sceneSlots.layoutManager = GridLayoutManager(context, 4)
        binding.sceneSlots.adapter = slotMinerAdapter
    }

    override fun onItemClick(position: Int, slot: Slot) {
        activity?.let {
            MinerHelper.updateStatusBalance(
                slot,
                FragmentMinerSecondGameBindingImpl(binding)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
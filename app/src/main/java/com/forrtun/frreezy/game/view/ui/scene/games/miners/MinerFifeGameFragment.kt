package com.forrtun.frreezy.game.view.ui.scene.games.miners

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentMinerFifeGameBinding
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.adapter.RecyclerSlotMinerAdapter
import com.forrtun.frreezy.game.view.adapter.SlotClickListener
import com.forrtun.frreezy.game.view.manager.music.BackgroundMusicManager
import com.forrtun.frreezy.game.view.manager.music.CustomMediaPlayer
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.ui.animation.AnimationUtil
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.FragmentMinerFifeGameBindingImpl
import com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers.MinerHelper

class MinerFifeGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentMinerFifeGameBinding
    private lateinit var backgroundMusic: BackgroundMusicManager
    private lateinit var slotMinerAdapter: RecyclerSlotMinerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMinerFifeGameBinding.inflate(layoutInflater, container, false)
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
        val originalSlotMutableList = MutableList(4) { R.drawable.ic_slot_5c }
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

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private fun setupBetButtons(buttons: List<ImageView>, betValues: List<Int>) {
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                it.startAnimation(AnimationUtil.loadButtonAnimation(requireContext()))
                binding.textBet.text = "BET\n${betValues[index]}"
            }
        }
    }

    private fun setSlotRecycler(originalSlotMutableList: MutableList<Int>) {
        slotMinerAdapter = RecyclerSlotMinerAdapter(originalSlotMutableList.map { Slot(it) }, 2)
        binding.sceneSlots.layoutManager = GridLayoutManager(context, 2)
        binding.sceneSlots.adapter = slotMinerAdapter
    }

    override fun onItemClick(position: Int, slot: Slot) {
        activity?.let {
            MinerHelper.updateStatusBalance(
                slot,
                FragmentMinerFifeGameBindingImpl(binding)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
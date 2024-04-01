package com.forrtun.frreezy.game.view.ui.scene.games.miners

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStake
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStakeUI
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog

class MinerSecondGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentMinerSecondGameBinding
    private var originalSlotMutableList = MutableList(12) { R.drawable.ic_slot_7b }
    private lateinit var slotList: List<Slot>
    private lateinit var slotMinerAdapter: RecyclerSlotMinerAdapter
    private var defaultBalance = 10000
    private lateinit var managerStatusStake: ManagerStatusStake
    private lateinit var backgroundMusic: BackgroundMusicManager
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
        setSlotRecycler()
        initControlButton()
        slotMinerAdapter.setSlotMinerClickListener(this)
        activity?.let { context ->
            if (isTotalSave()) {
                val (saveTotal) = getStatusStake(context)
                val total = convertStringToNumber(saveTotal.toString())
                binding.textBalance.text = "Total\n$total"
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
        var animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
        binding.btnSpin.setOnClickListener { it ->
            it!!.startAnimation(animation)
            if (defaultBalance <= 0) {
                return@setOnClickListener
            }
            slotList = originalSlotMutableList.map { Slot(it) }
            binding.sceneSlots.let { slotMinerAdapter.updateSlotList(slotList) }
        }
        binding.btnMinus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            managerStatusStake.decreaseBet()
            setStatusStakeUI(binding, managerStatusStake)
        }
        binding.btnPlus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            managerStatusStake.increaseBet()
            setStatusStakeUI(binding, managerStatusStake)
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

    private fun setSlotRecycler() {
        slotList = originalSlotMutableList.map { Slot(it) }
        slotMinerAdapter = RecyclerSlotMinerAdapter(slotList, 5)
        binding.sceneSlots.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = slotMinerAdapter
        }
    }

    private fun getWinCoefficient(slot: Slot): Float {
        return when (slot.image) {
            R.drawable.ic_slot_4b -> 4.5f
            R.drawable.ic_slot_5b -> 0.0f
            R.drawable.ic_slot_1b -> 1.0f
            R.drawable.ic_slot_3b -> 2.5f
            R.drawable.ic_slot_2b -> 3.5f
            R.drawable.ic_slot_6b -> 0.0f
            else -> 0.0f
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(position: Int, slot: Slot) {
        val bid = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        if (getWinCoefficient(slot).toInt() == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance.text = "Total\n$balance"
        } else {
            val newSumWin = bid * getWinCoefficient(slot).toInt()
            balance += newSumWin
            binding.textBalance.text = "Total\n$balance"
            win += newSumWin
            binding.textWin.text = "WIN\n$win"
        }
        activity?.let {
            setStatusStake(
                "Total\n$balance",
                bid.toString(),
                "WIN\n$win"
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.releaseMusicPlayer()
    }
}
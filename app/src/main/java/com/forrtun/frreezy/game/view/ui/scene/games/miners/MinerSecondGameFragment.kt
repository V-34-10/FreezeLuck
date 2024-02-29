package com.forrtun.frreezy.game.view.ui.scene.games.miners

import android.annotation.SuppressLint
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
import com.forrtun.frreezy.game.view.manager.ManagerStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.constructor
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStakeUI

class MinerSecondGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentMinerSecondGameBinding
    private var originalSlotMutableList = MutableList(12) { R.drawable.ic_slot_7b }
    private lateinit var slotList: List<Slot>
    private lateinit var slotMinerAdapter: RecyclerSlotMinerAdapter
    private var defaultBalance = 10000
    private lateinit var managerStatusStake: ManagerStatusStake
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMinerSecondGameBinding.inflate(layoutInflater, container, false)
        managerStatusStake =
            constructor(convertStringToNumber(binding.textBalance.text.toString()))
        setStatusStakeUI(binding, managerStatusStake)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSlotRecycler()
        initControlButton()
        slotMinerAdapter.setSlotMinerClickListener(this)
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
            managerStatusStake.minusBet()
            setStatusStakeUI(binding, managerStatusStake)
        }
        binding.btnPlus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            managerStatusStake.plusBet()
            setStatusStakeUI(binding, managerStatusStake)
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.button_animation)
            it.startAnimation(animation)
            activity?.onBackPressed()
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
package com.forrtun.frreezy.game.view.ui.scene.games

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.GridLayoutManager
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentSlotsFirstGameBinding
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.adapter.RecyclerSlotAdapter
import com.forrtun.frreezy.game.view.manager.ManagerStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.constructor
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.setStatusStakeUI

class SlotsFirstGameFragment : Fragment() {

    private lateinit var binding: FragmentSlotsFirstGameBinding
    private var originalSlotMutableList = mutableListOf(
        R.drawable.ic_slot_6a,
        R.drawable.ic_slot_5a,
        R.drawable.ic_slot_1a,
        R.drawable.ic_slot_5a,
        R.drawable.ic_slot_2a,
        R.drawable.ic_slot_6a,
        R.drawable.ic_slot_4a,
        R.drawable.ic_slot_2a,
        R.drawable.ic_slot_5a,
        R.drawable.ic_slot_5a,
        R.drawable.ic_slot_1a,
        R.drawable.ic_slot_6a,
        R.drawable.ic_slot_3a,
        R.drawable.ic_slot_6a,
        R.drawable.ic_slot_2a,
        R.drawable.ic_slot_5a,
        R.drawable.ic_slot_2a
    )
    private lateinit var slotList: List<Slot>
    private lateinit var slotAdapter: RecyclerSlotAdapter
    private val slotSize = slotAdapter.itemCount
    private var animationStart = false
    private var defaultBalance = 10000
    private lateinit var managerStatusStake: ManagerStatusStake
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSlotsFirstGameBinding.inflate(layoutInflater, container, false)
        managerStatusStake = constructor(convertStringToNumber(binding.textBalance.text.toString()))
        setStatusStakeUI(binding, managerStatusStake)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSlotRecycler()
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
            it.startAnimation(animation)
            if (animationStart || defaultBalance <= 0) {
                return@setOnClickListener
            }

            lockAnimation()
            slotAdapter.updateSlotList(emptyList())

            animation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    animationStart = true
                }

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(animation: Animation?) {
                    animationStart = false
                    calculateSpinSlot()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            getShuffleSlotList()
            slotAdapter.updateSlotList(slotList)
            slotAdapter.setAnimationSpin(binding.sceneSlots)
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

    @SuppressLint("SetTextI18n")
    private fun calculateSpinSlot() {
        val bid = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        if (getWinCoefficient(slotList).toInt() == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance.text = "Total\n$balance"
        } else {
            val newSumWin = bid * getWinCoefficient(slotList)
            balance += newSumWin.toInt()
            binding.textBalance.text = "Total\n$balance"
            win += newSumWin.toInt()
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

    private fun getShuffleSlotList() {
        originalSlotMutableList.shuffle()
        slotList = originalSlotMutableList.map { Slot(it) }
    }

    private fun getWinCoefficient(slotList: List<Slot>): Float {
        return when (slotList.subList(4, 12).distinct().size) {
            2 -> 0.5f
            3 -> 1.5f
            4 -> 2.5f
            else -> 0.0f
        }
    }

    private fun lockAnimation() {
        for (i in 0 until slotSize) {
            val holder =
                binding.sceneSlots.findViewHolderForAdapterPosition(i) as? RecyclerSlotAdapter.SlotViewHolder
            holder?.itemView?.clearAnimation()
        }
    }

    private fun setSlotRecycler() {
        slotList = originalSlotMutableList.map { Slot(it) }
        slotAdapter = RecyclerSlotAdapter(slotList)
        binding.sceneSlots.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = slotAdapter
        }
    }
}
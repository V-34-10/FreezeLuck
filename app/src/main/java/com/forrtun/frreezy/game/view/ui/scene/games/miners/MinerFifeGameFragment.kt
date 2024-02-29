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
import com.forrtun.frreezy.game.databinding.FragmentMinerFifeGameBinding
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.adapter.RecyclerSlotMinerAdapter
import com.forrtun.frreezy.game.view.adapter.SlotClickListener
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.getStatusStake
import com.forrtun.frreezy.game.view.manager.UpdateStatusStake.isTotalSave
import com.forrtun.frreezy.game.view.ui.dialog.StatusDialog.runDialog

class MinerFifeGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentMinerFifeGameBinding
    private var originalSlotMutableList = MutableList(4) { R.drawable.ic_slot_5c }
    private lateinit var slotList: List<Slot>
    private lateinit var slotMinerAdapter: RecyclerSlotMinerAdapter
    private var defaultBalance = 10000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMinerFifeGameBinding.inflate(layoutInflater, container, false)
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
        binding.btnSpin.setOnClickListener { it ->
            it!!.startAnimation(animation)
            if (defaultBalance <= 0) {
                return@setOnClickListener
            }
            slotList = originalSlotMutableList.map { Slot(it) }
            binding.sceneSlots.let { slotMinerAdapter.updateSlotList(slotList) }
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

    private fun setSlotRecycler() {
        slotList = originalSlotMutableList.map { Slot(it) }
        slotMinerAdapter = RecyclerSlotMinerAdapter(slotList, 2)
        binding.sceneSlots.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = slotMinerAdapter
        }
    }

    private fun getWinCoefficient(slot: Slot): Float {
        return when (slot.image) {
            R.drawable.ic_slot_1c -> 3.0f
            R.drawable.ic_slot_2c -> 0.0f
            R.drawable.ic_slot_3c -> 1.0f
            R.drawable.ic_slot_4c -> 0.0f
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
            UpdateStatusStake.setStatusStake(
                it1,
                "Total\n$balance",
                bid.toString(),
                "WIN\n$win"
            )
        }
    }
}
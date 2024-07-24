package com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers

import android.annotation.SuppressLint
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.convertStringToNumber
import com.forrtun.frreezy.game.view.manager.stake.UpdateStatusStake.setStatusStake

object MinerHelper {

    private fun getWinSecondCoefficient(slot: Slot): Float {
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

    private fun getWinFifeCoefficient(slot: Slot): Float {
        return when (slot.image) {
            R.drawable.ic_slot_1c -> 3.0f
            R.drawable.ic_slot_2c -> 0.0f
            R.drawable.ic_slot_3c -> 1.0f
            R.drawable.ic_slot_4c -> 0.0f
            else -> 0.0f
        }
    }

    private fun getWinFirstCoefficient(slotList: List<Slot>): Float {
        return when (slotList.subList(4, 12).distinct().size) {
            2 -> 0.5f
            3 -> 1.5f
            4 -> 2.5f
            else -> 0.0f
        }
    }

    @SuppressLint("SetTextI18n")
    fun updateStatusBalance(
        slot: Slot? = null,
        binding: GameMinerBinding,
        slotList: List<Slot> = emptyList()
    ) {
        val bid = convertStringToNumber(binding.textBet.text.toString())
        var win = convertStringToNumber(binding.textWin.text.toString())
        var balance = convertStringToNumber(binding.textBalance.text.toString())

        val coefficient = when (binding) {
            is FragmentMinerFifeGameBindingImpl -> slot?.let { getWinFifeCoefficient(it) }
            is FragmentMinerSecondGameBindingImpl -> slot?.let { getWinSecondCoefficient(it) }
            is FragmentSlotFirstGameBindingImpl -> getWinFirstCoefficient(slotList)
            else -> {
                0.0f
            }
        }

        coefficient?.let { coeff ->
            if (coeff.toInt() == 0) {
                balance -= bid
                win -= bid
                if (balance < 0) balance = 0
                if (win < 0) win = 0
            } else {
                val newSumWin = bid * coeff
                balance += newSumWin.toInt()
                win += newSumWin.toInt()
            }
            binding.textBalance.text = "Total\n$balance"
            binding.textWin.text = "WIN\n$win"
        }
        setStatusStake(
            "Total\n$balance",
            bid.toString(),
            "WIN\n$win"
        )
    }
}
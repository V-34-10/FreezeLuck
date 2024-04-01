package com.forrtun.frreezy.game.view.manager.stake

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.viewbinding.ViewBinding
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentMinerSecondGameBinding
import com.forrtun.frreezy.game.databinding.FragmentSlotsFirstGameBinding
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding

object UpdateStatusStake {
    private lateinit var sharedPreferences: SharedPreferences
    fun constructor(context: Context, sumStake: Int): ManagerStatusStake {
        sharedPreferences = context.getSharedPreferences("FortuneFreezyPref", Context.MODE_PRIVATE)
        val minStake = 0.01
        val maxStake = 1.0
        val stepStake = 100
        return ManagerStatusStake(sumStake, minStake, maxStake, stepStake)
    }

    fun convertStringToNumber(text: String): Int = text.replace(Regex("\\D"), "").toIntOrNull() ?: 0

    @SuppressLint("SetTextI18n")
    fun setStatusStakeUI(binding: ViewBinding, stake: ManagerStatusStake) {
        val defaultStake = stake.defaultBet
        when (binding) {
            is FragmentSlotsFirstGameBinding -> {
                binding.textBet.text = "BET\n$defaultStake"
                binding.btnMinus.isEnabled = stake.canDecreaseBet
                binding.btnPlus.isEnabled = stake.canIncreaseBet
            }

            is FragmentMinerSecondGameBinding -> {
                binding.textBet.text = "BET\n$defaultStake"
                binding.btnMinus.isEnabled = stake.canDecreaseBet
                binding.btnPlus.isEnabled = stake.canIncreaseBet
            }

            is FragmentWheelThreeGameBinding -> {
                binding.textBet.text = "BET\n$defaultStake"
            }
        }
    }

    fun setStatusStake(total: String, bet: String?, win: String?) {
        sharedPreferences.edit().apply {
            putString("total", total)
            bet?.let { putString("bet", it) }
            win?.let { putString("win", it) }
            apply()
        }
    }

    fun getStatusStake(context: Context): Triple<String?, String?, String?> {
        return Triple(
            sharedPreferences.getString("total", context.getString(R.string.default_total)),
            sharedPreferences.getString("bet", context.getString(R.string.default_bet)),
            sharedPreferences.getString("win", context.getString(R.string.default_win))
        )
    }

    fun isTotalSave(): Boolean = sharedPreferences.contains("total")
}
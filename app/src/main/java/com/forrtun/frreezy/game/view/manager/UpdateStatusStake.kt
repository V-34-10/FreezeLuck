package com.forrtun.frreezy.game.view.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.FragmentMinerSecondGameBinding
import com.forrtun.frreezy.game.databinding.FragmentSlotsFirstGameBinding
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding

object UpdateStatusStake {
    private lateinit var sharedPreferences: SharedPreferences
    fun constructor(sumStake: Int): ManagerStatusStake {
        val minStake = 0.01
        val maxStake = 1.0
        val stepStake = 100
        return ManagerStatusStake(sumStake, minStake, maxStake, stepStake)
    }

    fun convertStringToNumber(text: String): Int = text.replace(Regex("\\D"), "").toIntOrNull() ?: 0

    fun setStatusStakeUI(binding: ViewBinding, stake: ManagerStatusStake) {
        val defaultStake = stake.getDefaultBet()
        when (binding) {
            is FragmentSlotsFirstGameBinding -> {
                binding.textBet.text = defaultStake.toString()
                binding.btnMinus.isEnabled = stake.isMinusBet()
                binding.btnPlus.isEnabled = stake.isPlusBet()
            }

            is FragmentMinerSecondGameBinding -> {
                binding.textBet.text = defaultStake.toString()
                binding.btnMinus.isEnabled = stake.isMinusBet()
                binding.btnPlus.isEnabled = stake.isPlusBet()
            }

            is FragmentWheelThreeGameBinding -> {
                binding.textBet.text = defaultStake.toString()
            }
        }
    }

    fun setStatusStake(context: Context, total: String, bet: String?, win: String?) {
        sharedPreferences =
            context.getSharedPreferences("FortuneFreezyPref", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("total", total)
            putString("bet", bet)
            putString("win", win)
            apply()
        }
    }

    fun getStatusStake(context: Context): Triple<String?, String?, String?> {
        sharedPreferences =
            context.getSharedPreferences("FortuneFreezyPref", AppCompatActivity.MODE_PRIVATE)
        val total = sharedPreferences.getString(
            "total",
            context.getString(R.string.default_total)
        )
        val bet = sharedPreferences.getString("bet", context.getString(R.string.default_bet))
        val win =
            sharedPreferences.getString("win", context.getString(R.string.default_win))
        return Triple(total, bet, win)
    }

    fun isTotalSave(context: Context): Boolean {
        sharedPreferences =
            context.getSharedPreferences("FortuneFreezyPref", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.contains("total")
    }
}
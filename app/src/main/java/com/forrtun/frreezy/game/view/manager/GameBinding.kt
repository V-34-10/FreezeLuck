package com.forrtun.frreezy.game.view.manager

import android.widget.ImageView
import android.widget.TextView
import com.forrtun.frreezy.game.databinding.FragmentWheelFourGameBinding
import com.forrtun.frreezy.game.databinding.FragmentWheelThreeGameBinding

interface GameBinding {
    val textBet: TextView
    val textWin: TextView
    val textBalance: TextView
    val wheel: ImageView
}

class FragmentWheelThreeGameBindingImpl(private val binding: FragmentWheelThreeGameBinding) :
    GameBinding {
    override val textBet: TextView
        get() = binding.textBet
    override val textWin: TextView
        get() = binding.textWin
    override val textBalance: TextView
        get() = binding.textBalance

    override val wheel: ImageView
        get() = binding.wheel
}

class FragmentWheelFourGameBindingImpl(private val binding: FragmentWheelFourGameBinding) :
    GameBinding {
    override val textBet: TextView
        get() = binding.textBet
    override val textWin: TextView
        get() = binding.textWin
    override val textBalance: TextView
        get() = binding.textBalance

    override val wheel: ImageView
        get() = binding.wheel
}
package com.forrtun.frreezy.game.view.ui.scene.games.miners.helpers

import android.widget.TextView
import com.forrtun.frreezy.game.databinding.FragmentMinerFifeGameBinding
import com.forrtun.frreezy.game.databinding.FragmentMinerSecondGameBinding

interface GameMinerBinding {
    val textBet: TextView
    val textWin: TextView
    val textBalance: TextView
}

class FragmentMinerFifeGameBindingImpl(private val binding: FragmentMinerFifeGameBinding) :
    GameMinerBinding {
    override val textBet: TextView
        get() = binding.textBet
    override val textWin: TextView
        get() = binding.textWin
    override val textBalance: TextView
        get() = binding.textBalance
}

class FragmentMinerSecondGameBindingImpl(private val binding: FragmentMinerSecondGameBinding) :
    GameMinerBinding {
    override val textBet: TextView
        get() = binding.textBet
    override val textWin: TextView
        get() = binding.textWin
    override val textBalance: TextView
        get() = binding.textBalance
}
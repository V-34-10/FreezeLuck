package com.forrtun.frreezy.game.view.adapter

import com.forrtun.frreezy.game.model.Slot

interface SlotClickListener {
    fun onItemClick(position: Int, slot: Slot)
}
package com.forrtun.frreezy.game.view.callback

import androidx.recyclerview.widget.DiffUtil
import com.forrtun.frreezy.game.model.Slot

class DiffCallbackSlot(
    private val originalSlotList: List<Slot>,
    private val updateSlotList: List<Slot>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = originalSlotList.size

    override fun getNewListSize(): Int = updateSlotList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        originalSlotList[oldItemPosition].image == updateSlotList[newItemPosition].image

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        originalSlotList[oldItemPosition] == updateSlotList[newItemPosition]
}
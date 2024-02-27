package com.forrtun.frreezy.game.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.model.Slot
import com.forrtun.frreezy.game.view.callback.DiffCallbackSlot

class RecyclerSlotAdapter(private var slotRecyclerList: List<Slot>) :
    RecyclerView.Adapter<RecyclerSlotAdapter.SlotViewHolder>() {
    private var originalSlotList: List<Slot> = emptyList()

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotImageView: ImageView = itemView.findViewById(R.id.imageRecyclerSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun getItemCount(): Int = slotRecyclerList.size

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slotRecyclerList[position]
        holder.slotImageView.setImageResource(slot.image)
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slot_animation)
        )
    }

    fun updateSlotList(updateSlotList: List<Slot>) {
        originalSlotList = slotRecyclerList.toList()
        slotRecyclerList = updateSlotList
        val diffCallbackSlot = DiffCallbackSlot(originalSlotList, updateSlotList)
        val result = DiffUtil.calculateDiff(diffCallbackSlot)
        result.dispatchUpdatesTo(this)
    }

    fun setAnimationSpin(recyclerView: RecyclerView) {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? SlotViewHolder
            holder?.itemView?.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slot_animation)
            )
        }
    }
}
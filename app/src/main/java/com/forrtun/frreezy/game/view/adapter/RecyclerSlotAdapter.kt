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

class RecyclerSlotAdapter(private var slotRecyclerList: List<Slot>) :
    RecyclerView.Adapter<RecyclerSlotAdapter.SlotViewHolder>() {
    private var originalSlotList: List<Slot> = emptyList()

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val slotImageView: ImageView = itemView.findViewById(R.id.imageRecyclerSlot)
        fun bind(slot: Slot) {
            slotImageView.setImageResource(slot.image)
            startAnimation()
        }

        private fun startAnimation() {
            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.slot_animation)
            itemView.startAnimation(animation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun getItemCount(): Int = slotRecyclerList.size

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slotRecyclerList[position]
        holder.bind(slot)
    }

    fun resetSlotList(updateSlotList: List<Slot>) {
        originalSlotList = slotRecyclerList.toList()
        slotRecyclerList = updateSlotList
        val diffCallbackSlot = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = originalSlotList.size

            override fun getNewListSize(): Int = updateSlotList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                originalSlotList[oldItemPosition].image == updateSlotList[newItemPosition].image

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                originalSlotList[oldItemPosition] == updateSlotList[newItemPosition]
        }
        val result = DiffUtil.calculateDiff(diffCallbackSlot)
        result.dispatchUpdatesTo(this)
    }

    fun setAnimationSpin() {
        for (i in 0 until itemCount) {
            notifyItemChanged(i)
        }
    }

}
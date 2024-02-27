package com.forrtun.frreezy.game.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.model.Slot

class RecyclerSlotMinerAdapter(
    private var defaultList: List<Slot>,
    private val maxOpenSlot: Int
) :
    RecyclerView.Adapter<RecyclerSlotMinerAdapter.SlotViewHolder>() {
    private var slotMutableList: MutableList<Int>? = null
    private var itemSlotMinerClickListener: SlotClickListener? = null
    private var slotOpenCounter = 0
    private var imageSlot: Int = 0
    private var imgSlot: Int = 0

    init {
        slotMutableList = mutableListOf(
            R.drawable.ic_slot_4b,
            R.drawable.ic_slot_5b,
            R.drawable.ic_slot_1b,
            R.drawable.ic_slot_3b,
            R.drawable.ic_slot_2b,
            R.drawable.ic_slot_3b,
            R.drawable.ic_slot_4b,
            R.drawable.ic_slot_1b,
            R.drawable.ic_slot_1b,
            R.drawable.ic_slot_1b,
            R.drawable.ic_slot_6b,
            R.drawable.ic_slot_3b
        )
        imageSlot = R.layout.item_slot_miner
        imgSlot = R.id.imageRecyclerSlotMiner
    }

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotImage: ImageView = itemView.findViewById(imgSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(imageSlot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slotItem = defaultList[position]
        holder.slotImage.setImageResource(slotItem.image)
        holder.itemView.setOnClickListener {
            setOnClickHandleListener(holder, position)
        }
    }

    override fun getItemCount(): Int = defaultList.size

    fun setSlotMinerClickListener(listener: SlotClickListener) {
        itemSlotMinerClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSlotList(newSlotList: List<Slot>) {
        defaultList = newSlotList
        notifyDataSetChanged()
        resetOpenSlotCount()
    }

    private fun setOnClickHandleListener(holder: SlotViewHolder, position: Int) {
        if (slotOpenCounter < maxOpenSlot) {
            slotMutableList?.shuffle()
            val randomImage = slotMutableList?.randomOrNull()
            if (randomImage != null) {
                holder.slotImage.setImageResource(randomImage)
                itemSlotMinerClickListener?.onItemClick(position, Slot(randomImage))
            }
            slotOpenCounter++
        }
    }

    private fun resetOpenSlotCount() {
        slotOpenCounter = 0
    }
}
package com.example.emaillinkauthrnd

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*

class ItemAdapter(items: ArrayList<String>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    var itemList = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.itemView.tvContent.text = currentItem

        holder.itemView.btnShare.setOnClickListener {
            onItemClickListener?.let { it(currentItem) }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
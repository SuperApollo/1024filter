package com.example.clfilter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_layout.view.*

class MyAdapter(
    private var onItemLongClickListener: ItemLongClickListener? = null,
    var onlineBeans: MutableList<OnlineBean>
) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return onlineBeans.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val onlineBean = onlineBeans[position]
        holder.itemView.tv_item_name.text = onlineBean.name + "   评论:${onlineBean.comments}"
        holder.itemView.tv_item_response_count.text = onlineBean.comments
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener?.onItemLongClick(position, onlineBean)
            true
        }
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
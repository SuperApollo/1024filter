package com.example.clfilter

interface ItemClickListener {
    fun onItemLongClick(position: Int, bean: OnlineBean)
    fun onItemClick(position: Int, bean: OnlineBean)
}
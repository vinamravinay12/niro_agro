package com.niro.niroapp.adapters

import androidx.recyclerview.widget.RecyclerView


interface RecyclerScrollDirectionListener {
    fun onScrolledDown(isDown : Boolean)
}
class RecyclerScrollListener(private val recyclerScrollDirectionListener: RecyclerScrollDirectionListener) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        recyclerScrollDirectionListener.onScrolledDown(dy > 0)
    }
}
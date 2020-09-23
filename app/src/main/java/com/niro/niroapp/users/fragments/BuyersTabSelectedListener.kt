package com.niro.niroapp.users.fragments

import com.google.android.material.tabs.TabLayout

interface OnTabSelectedDelegate {
    fun onTabSelected(tab: TabLayout.Tab?)
}

class TabSelectedListener(private val tabSelectedListener : OnTabSelectedDelegate)  : TabLayout.OnTabSelectedListener {

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabSelectedListener.onTabSelected(tab)
    }
}
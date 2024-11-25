package com.example.basilisk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProgressBarAdapter(
    fragmentActivity: FragmentActivity,
    private val progressValues: List<Int>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = progressValues.size

    override fun createFragment(position: Int): Fragment {
        return ProgressBarFragment.newInstance(progressValues[position])
    }
}
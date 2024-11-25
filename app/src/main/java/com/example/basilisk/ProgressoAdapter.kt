package com.example.basilisk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProgressoAdapter(fragmentActivity: AppCompatActivity, private val progressoList: List<String>) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = progressoList.size

    override fun createFragment(position: Int): Fragment {
        return ProgressoFragment.newInstance(progressoList[position])
    }
}


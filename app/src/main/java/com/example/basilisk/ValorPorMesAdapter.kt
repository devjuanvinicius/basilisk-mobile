package com.example.basilisk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ValorPorMesAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val valorPorMesList: MutableList<Double> = mutableListOf()

    override fun getItemCount(): Int {
        return valorPorMesList.size
    }

    override fun createFragment(position: Int): Fragment {
        val valorPorMes = valorPorMesList[position]
        return ValorPorMesFragment.newInstance(valorPorMes)
    }

    fun submitList(valorPorMes: List<Double>) {
        valorPorMesList.clear()
        valorPorMesList.addAll(valorPorMes)
        notifyDataSetChanged()
    }
}

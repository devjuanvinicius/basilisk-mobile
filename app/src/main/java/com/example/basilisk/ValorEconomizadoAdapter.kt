package com.example.basilisk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ValorEconomizadoAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val valorEconomizadoList: MutableList<Double> = mutableListOf()

    override fun getItemCount(): Int {
        return valorEconomizadoList.size
    }

    override fun createFragment(position: Int): Fragment {
        val valorEconomizado = valorEconomizadoList[position]
        return ValorEconomizadoFragment.newInstance(valorEconomizado)
    }

    fun submitList(valorEconomizado: List<Double>) {
        valorEconomizadoList.clear()
        valorEconomizadoList.addAll(valorEconomizado)
        notifyDataSetChanged()
    }
}
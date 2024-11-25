package com.example.basilisk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MetasAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var metas = listOf<Triple<String, Double, Int>>() // Lista de metas (Título, Valor, Meses)

    // Atualiza a lista de metas
    fun submitList(newMetas: List<Triple<String, Double, Int>>) {
        metas = newMetas
        notifyDataSetChanged() // Notifica a mudança para o adapter
    }

    override fun getItemCount(): Int {
        return metas.size
    }

    override fun createFragment(position: Int): Fragment {
        val meta = metas[position]
        val tituloMeta = meta.first
        val valorMeta = meta.second
        val qtnMeses = meta.third

        // Calcula o valor mensal apenas se o número de meses for maior que 0
        val valorMensal = if (qtnMeses > 0) valorMeta / qtnMeses else 0.0

        // Passa os valores para o fragmento
        return MetaFragment.newInstance(tituloMeta, valorMeta, valorMensal)
    }
}

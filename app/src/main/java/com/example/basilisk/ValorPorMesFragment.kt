package com.example.basilisk

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.basilisk.databinding.FragmentValorPorMesBinding

class ValorPorMesFragment : Fragment(R.layout.fragment_valor_por_mes) {
    private var valorPorMes: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obter o valor por mês do Bundle
        arguments?.let {
            valorPorMes = it.getDouble("valorPorMes")
        }

        // Atualizar o UI (exemplo: TextView)
        val textView = view.findViewById<TextView>(R.id.textViewValorPorMes)
        textView.text = "R$ ${"%.2f".format(valorPorMes)}"
    }

    companion object {
        fun newInstance(valorPorMes: Double): ValorPorMesFragment {
            val fragment = ValorPorMesFragment()
            val bundle = Bundle()
            bundle.putDouble("valorPorMes", valorPorMes)
            fragment.arguments = bundle
            return fragment
        }
    }
}

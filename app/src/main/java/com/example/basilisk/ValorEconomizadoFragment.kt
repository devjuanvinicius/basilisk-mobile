package com.example.basilisk
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.basilisk.databinding.FragmentValorEconomizadoBinding

class ValorEconomizadoFragment : Fragment(R.layout.fragment_valor_economizado) {
    private var valorEconomizado: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obter o valor economizado do Bundle
        arguments?.let {
            valorEconomizado = it.getDouble("valorEconomizado")
        }

        // Atualizar o UI (exemplo: TextView)
        val textView = view.findViewById<TextView>(R.id.textViewValorEconomizado)
        textView.text = "R$ ${"%.2f".format(valorEconomizado)}"
    }

    companion object {
        fun newInstance(valorEconomizado: Double): ValorEconomizadoFragment {
            val fragment = ValorEconomizadoFragment()
            val bundle = Bundle()
            bundle.putDouble("valorEconomizado", valorEconomizado)
            fragment.arguments = bundle
            return fragment
        }
    }
}
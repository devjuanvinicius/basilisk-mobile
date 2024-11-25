package com.example.basilisk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.basilisk.R
import com.example.basilisk.databinding.FragmentMetaBinding
class MetaFragment : Fragment(R.layout.fragment_meta) {

    companion object {
        // Cria uma nova instância do fragmento com os dados da meta, incluindo o valor mensal
        fun newInstance(tituloMeta: String, valorMeta: Double, valorMensal: Double): MetaFragment {
            val fragment = MetaFragment()
            val args = Bundle().apply {
                putString("tituloMeta", tituloMeta)
                putDouble("valorMeta", valorMeta)
                putDouble("valorMensal", valorMensal)  // Passando o valor mensal
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tituloMeta: String
    private var valorMeta: Double = 0.0
    private var valorMensal: Double = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recupera os dados passados para o fragmento
        arguments?.let {
            tituloMeta = it.getString("tituloMeta", "")
            valorMeta = it.getDouble("valorMeta", 0.0)
            valorMensal = it.getDouble("valorMensal", 0.0)  // Recuperando o valor mensal
        }

        // Exibe as informações no layout do fragmento
        view.findViewById<TextView>(R.id.textNomeMeta).text = tituloMeta
        view.findViewById<TextView>(R.id.textMeta).text = "Meta: R$ ${"%.2f".format(valorMeta)}"
        view.findViewById<TextView>(R.id.textValorMensal).text = "R$ ${"%.2f".format(valorMensal)}"  // Exibe o valor mensal
    }
}


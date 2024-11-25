package com.example.basilisk

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProgressoFragment : Fragment(R.layout.fragment_progresso) {

    companion object {
        private const val PROGRESSO_KEY = "progresso_key"

        // Função para criar uma nova instância do fragmento com o progresso da meta
        fun newInstance(progresso: String): ProgressoFragment {
            val fragment = ProgressoFragment()
            val bundle = Bundle()
            bundle.putString(PROGRESSO_KEY, progresso)
            fragment.arguments = bundle
            return fragment
        }
    }

    // Acessa o valor do progresso a partir dos argumentos
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progresso = arguments?.getString(PROGRESSO_KEY)
        val progressoTextView: TextView = view.findViewById(R.id.progressoTextView)
        progressoTextView.text = progresso
    }
}

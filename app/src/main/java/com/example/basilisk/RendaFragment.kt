package com.example.basilisk

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
//
class RendaFragment : Fragment(R.layout.fragment_renda) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exemplo de uso da função: você pode vinculá-la a um botão ou chamá-la diretamente.
        view.findViewById<View>(R.id.editarRenda)?.setOnClickListener {
            irParaRendas()
        }
    }

    private fun irParaRendas() {
        val intent = Intent(requireContext(), TotalRendas::class.java)
        startActivity(intent)
    }
}

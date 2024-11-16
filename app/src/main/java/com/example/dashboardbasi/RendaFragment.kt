package com.example.dashboardbasi

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

class RendaFragment : Fragment(R.layout.fragment_renda) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Carregar fonte personalizada da pasta assets/fonts
        val customFont = Typeface.createFromAsset(requireContext().assets, "fonts/custom_font.ttf")

        // Configurando o Spinner
        val spinner: Spinner = view.findViewById(R.id.spinnermes)
        val meses = resources.getStringArray(R.array.meses)
        val mesesabv = arrayOf(
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // TextView para exibir o mês selecionado em abreviação
        val selectedMonthTextView: TextView = view.findViewById(R.id.selectedMonth)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedMonthTextView.text = mesesabv[position]
                selectedMonthTextView.typeface = customFont // Aplica a fonte customizada
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nenhuma ação necessária
            }
        }
    }
}

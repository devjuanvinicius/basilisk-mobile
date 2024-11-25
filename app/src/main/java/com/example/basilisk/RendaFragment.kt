package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.model.Renda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class RendaFragment : Fragment(R.layout.fragment_renda) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val rendasDAO = RendaDAO(db)
        var rendaLista: List<Renda> = listOf()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            rendasDAO.retornarRenda(
                currentUser.uid,
                onSuccess = { rendas ->
                    rendaLista = rendas

                    val textView: TextView = view.findViewById(R.id.totalrendas)
                    val totalRenda = calcularTotalRenda(rendaLista)
                    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

                    textView.text = formatador.format(totalRenda)

                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Erro ao carregar rendas: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {

        }

        view.findViewById<View>(R.id.editarRenda)?.setOnClickListener {
            irParaRendas()
        }
    }

    private fun calcularTotalRenda(rendaLista: List<Renda>): Any {

        var total = 0.0

        for (renda in rendaLista) {
            total += renda.valor
        }

        return total
    }

    private fun irParaRendas() {
        val intent = Intent(requireContext(), TotalRendas::class.java)
        startActivity(intent)
    }
}
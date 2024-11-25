package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.model.Despesas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DespesaFragment : Fragment(R.layout.fragment_despesa) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val despesasDAO = DespesasDAO(db, auth)
        var despesaLista: List<Despesas> = listOf()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            despesasDAO.retornarDespesa(
                currentUser.uid,
                onSuccess = { despesas ->
                    despesaLista = despesas
                    atualizarDadosTela(view, despesaLista)
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Erro ao carregar despesas: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Usuário não autenticado, trate aqui
        }

        // Configura o clique para ir à tela de adicionar despesa
        view.findViewById<View>(R.id.novaDespesa)?.setOnClickListener {
            irParaNovaDespesa()
        }
    }

    private fun atualizarDadosTela(view: View, despesaLista: List<Despesas>) {
        val totalDespesas: TextView = view.findViewById(R.id.totaldespesas)
        val valorDespesas = despesaLista.sumOf { it.valor.toDouble() }
        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        totalDespesas.text = formatador.format(valorDespesas)
    }

    private fun irParaNovaDespesa() {
        val intent = Intent(requireContext(), novaDespesa::class.java)
        startActivity(intent)
    }
}

package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.databinding.ActivityEditMetaBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditMeta : AppCompatActivity() {

    private lateinit var binding: ActivityEditMetaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditMetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste do padding da tela para não sobrepor as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recuperando os dados da Intent
        val metaId = intent.getStringExtra("metaId")
        val tituloMeta = intent.getStringExtra("tituloMeta")

        // Exibir valores recuperados para depuração
        println("metaId: $metaId, tituloMeta: $tituloMeta")

        if (metaId == null) {
            Toast.makeText(this, "Meta não encontrada para edição.", Toast.LENGTH_SHORT).show()
            finish() // Finaliza a activity se metaId for nulo
            return
        }

        // Preenchendo o campo com o título da meta atual
        binding.tituloMeta.setText(tituloMeta)

        // Configuração do clique do botão de voltar para a tela Cofrinho
        binding.voltarCofinho.setOnClickListener {
            val intent = Intent(this, CofrinhoActivity::class.java)
            startActivity(intent)
        }

        // Configuração do clique do botão de excluir a meta
        binding.excluirMeta.setOnClickListener {
            val tituloMetaParaExcluir = binding.tituloMeta.text.toString()

            if (tituloMetaParaExcluir.isNotEmpty()) {
                excluirMetaPorTitulo(tituloMetaParaExcluir)
            } else {
                Toast.makeText(this, "Informe o título da meta para excluir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para excluir a meta com base no título
    private fun excluirMetaPorTitulo(tituloMeta: String) {
        val db = FirebaseFirestore.getInstance()
        val metasRef = db.collection("Metas")

        metasRef.whereEqualTo("tituloMeta", tituloMeta).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Meta não encontrada!", Toast.LENGTH_SHORT).show()
                } else {
                    // Encontrou o documento, excluindo-o
                    for (document in documents) {
                        val metaId = document.id // Obtém o ID do documento
                        metasRef.document(metaId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Meta excluída com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, CofrinhoActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao excluir a meta: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar a meta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

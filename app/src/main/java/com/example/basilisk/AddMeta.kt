package com.example.basilisk

import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.databinding.ActivityAddMetaBinding
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull
import kotlin.to

class AddMeta : AppCompatActivity() {
    private lateinit var binding: ActivityAddMetaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddMetaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Verificar autenticação do usuário
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado! Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.voltarCofinho.setOnClickListener {
            intent = Intent(this, CofrinhoActivity::class.java)
            startActivity(intent)
        }

        binding.SalvarMeta.setOnClickListener {
            salvarMeta()
        }
    }

    private fun salvarMeta() {
        val tituloMeta = binding.tituloMeta.text.toString()
        val valorMeta = binding.valorMeta.text.toString().toDoubleOrNull()
        val qtnMeses = binding.qtnMeses.text.toString().toIntOrNull() // Adicionado campo qtnMeses
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Validações básicas
        if (tituloMeta.isEmpty() || valorMeta == null || qtnMeses == null) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (valorMeta <= 0) {
            Toast.makeText(this, "Por favor, insira um valor válido para a meta!", Toast.LENGTH_SHORT).show()
            return
        }

        if (qtnMeses <= 0) {
            Toast.makeText(this, "Por favor, insira uma quantidade válida de meses!", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria um objeto de meta
        val meta = mapOf(
            "tituloMeta" to tituloMeta,
            "valorMeta" to valorMeta,
            "qtnMeses" to qtnMeses,  // Salva a quantidade de meses
            "userId" to userId
        )

        // Salva no Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("Metas")
            .add(meta)
            .addOnSuccessListener {
                Toast.makeText(this, "Meta salva com sucesso!", Toast.LENGTH_SHORT).show()
                binding.tituloMeta.text.clear()
                binding.valorMeta.text.clear()
                binding.qtnMeses.text.clear() // Limpa o campo de meses
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar a meta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

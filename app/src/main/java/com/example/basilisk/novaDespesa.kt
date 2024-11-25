package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.databinding.ActivityNovaDespesaBinding
import com.example.basilisk.model.Despesas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import java.util.UUID

class novaDespesa : AppCompatActivity() {

    private lateinit var binding: ActivityNovaDespesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o View Binding
        binding = ActivityNovaDespesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val despesasDAO = DespesasDAO(db, auth)

        // Evento de clique do botão adicionar
        binding.button3.setOnClickListener {

            val titulo = binding.inputTituloDespesa.text.toString()
            val valor = binding.inputValorDespesa.text.toString().toDoubleOrNull()
            val dataPagamento = binding.inputDataFinal.text.toString()

            if (titulo.isNotEmpty() && valor != null && dataPagamento.isNotEmpty()) {
                val despesaFixa = binding.switch1.isChecked
                val idAleatorio = UUID.randomUUID().toString()


                val novaDespesa = Despesas(
                    id = idAleatorio,
                    nome = titulo,
                    valor = valor,
                    despesaFixa = despesaFixa,
                    dataPagamento = dataPagamento,
                    parcelas = 1
                )

                val idUsuario = auth.currentUser?.uid ?: ""
                if (idUsuario.isNotEmpty()) {
                    despesasDAO.criarDespesa(
                        idUsuario,
                        novaDespesa,
                        onSuccess = {
                            Toast.makeText(
                                this,
                                "Despesa adicionada com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish() // Fecha a Activity
                        },
                        onFailure = { exception ->
                            Toast.makeText(
                                this,
                                "Erro ao adicionar despesa: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Função para redirecionar para a tela principal
    fun irParaDash(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }
}

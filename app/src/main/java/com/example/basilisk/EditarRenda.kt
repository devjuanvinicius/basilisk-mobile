package com.example.basilisk

import android.icu.text.NumberFormat
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.databinding.ActivityEditarRendaBinding
import com.example.basilisk.model.Renda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class EditarRenda : AppCompatActivity() {

    private lateinit var binding: ActivityEditarRendaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding para acessar os elementos do layout
        binding = ActivityEditarRendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtém os dados enviados pela intent
        val idRenda = intent.getStringExtra("idRenda")
        val nome = intent.getStringExtra("nome")
        val valor = intent.getDoubleExtra("valor", 0.0)
        val dataRecebimento = intent.getStringExtra("dataRecebimento")

        // Verifica se o nome não é nulo e preenche o EditText de título
        nome?.let {
            binding.inputTituloEditRenda.setText(it)
        } ?: run {
            Log.e("EditarRenda", "Nome não recebido!")
        }

        // Exibe o valor formatado no EditText
        val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
        binding.inputValorEditRenda.setText(valorFormatado)

        // Preenche os outros campos com os dados da renda
        binding.inputDataInicio.setText(dataRecebimento)

        // Evento de clique do botão salvar
        binding.buttoneditar.setOnClickListener {
            val novoNome = binding.inputTituloEditRenda.text.toString()
            val novoValor = binding.inputValorEditRenda.text.toString().toDoubleOrNull() // Pega o valor real sem formatação
            val novaData = binding.inputDataInicio.text.toString()

            if (novoNome.isNotEmpty() && novoValor != null && novaData.isNotEmpty() && idRenda != null) {
                // Cria a renda atualizada
                val rendaAtualizada = Renda(
                    id = idRenda,
                    nome = novoNome,
                    valor = novoValor,
                    rendaFixa = binding.switch1.isChecked,
                    dataRecebimento = novaData
                )

                // Instância do RendaDAO
                val rendaDAO = RendaDAO(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
                val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (idUsuario.isNotEmpty()) {
                    // Chama o método editarRenda
                    rendaDAO.editarRenda(
                        idUsuario,
                        rendaAtualizada,
                        onSuccess = {
                            Toast.makeText(this, "Renda atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            finish() // Volta para a tela anterior
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, "Erro ao atualizar renda: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

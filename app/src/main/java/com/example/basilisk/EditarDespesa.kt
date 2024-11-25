package com.example.basilisk

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.databinding.ActivityEditarDespesaBinding
import com.example.basilisk.model.Despesas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.util.Locale

class EditarDespesa : AppCompatActivity() {

    private lateinit var binding: ActivityEditarDespesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding para acessar os elementos do layout
        binding = ActivityEditarDespesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtém os dados enviados pela intent
        val idDespesa = intent.getStringExtra("idDespesa")
        val titulo = intent.getStringExtra("titulo")
        val valor = intent.getDoubleExtra("valor", 0.0) // Recebe como String
        val dataPagamento = intent.getStringExtra("dataPagamento")

        // Formata o valor recebido para exibição
        titulo?.let {
            binding.inputValorEditDespesa.setText(it)
        } ?: run {
            Log.e("EditarRenda", "Nome não recebido!")
        }

        // Adiciona o TextWatcher corrigido
        binding.inputDataFinal.addTextChangedListener(DateTextWatcher(binding.inputDataFinal))
        val valorFormatado = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
        binding.inputValorEditDespesa.setText(valorFormatado)


        // Preenche os campos com os dados da despesa
        binding.inputTituloEditDespesa.setText(titulo)
        binding.inputValorEditDespesa.text.toString().toDoubleOrNull()  // Ajusta o valor inicial
        binding.inputDataFinal.setText(dataPagamento)

        // Evento de clique do botão salvar
        binding.ButtonEditdespesa.setOnClickListener {
            val novoTitulo = binding.inputTituloEditDespesa.text.toString()
            val novoValor = binding.inputValorEditDespesa.text.toString().toDoubleOrNull() // Corrige a escala
            val novaData = binding.inputDataFinal.text.toString()

            if (novoTitulo.isNotEmpty() && novoValor != null && novaData.isNotEmpty() && idDespesa != null) {
                // Cria a despesa atualizada
                val despesaAtualizada = Despesas(
                    id = idDespesa,
                    nome = novoTitulo,
                    valor = novoValor, // Utiliza o valor diretamente
                    despesaFixa = binding.switch1.isChecked,
                    dataPagamento = novaData,
                    parcelas = 1
                )


                // Instância do DespesasDAO
                val despesasDAO = DespesasDAO(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance())
                val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (idUsuario.isNotEmpty()) {
                    // Chama o método editarDespesa
                    despesasDAO.editarDespesa(
                        idUsuario,
                        despesaAtualizada,
                        onSuccess = {
                            Toast.makeText(this, "Despesa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            finish() // Volta para a tela anterior
                        },
                        onFailure = { exception ->
                            Toast.makeText(this, "Erro ao atualizar despesa: ${exception.message}", Toast.LENGTH_SHORT).show()
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

    fun irParaDash(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }


    // Formata o valor para exibição inicial no campo
    private fun formatarValor(valor: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return numberFormat.format(valor)
    }


    class DateTextWatcher(private val editText: EditText?) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            editText?.removeTextChangedListener(this)

            val cleanString = editable.toString().replace(Regex("[^\\d]"), "") // Remove qualquer caractere não numérico

            // Adiciona as barras (/) a cada dois números
            val formattedString = when {
                cleanString.length >= 8 -> cleanString.substring(0, 2) + "/" + cleanString.substring(2, 4) + "/" + cleanString.substring(4, 8)
                cleanString.length >= 6 -> cleanString.substring(0, 2) + "/" + cleanString.substring(2, 4) + cleanString.substring(4)
                cleanString.length >= 4 -> cleanString.substring(0, 2) + "/" + cleanString.substring(2)
                else -> cleanString
            }

            editText?.setText(formattedString)
            editText?.setSelection(formattedString.length)
            editText?.addTextChangedListener(this)
        }
    }

}


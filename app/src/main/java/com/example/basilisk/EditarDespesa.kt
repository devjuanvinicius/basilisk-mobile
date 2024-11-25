package com.example.basilisk

import android.content.Intent
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
import java.util.Locale

class EditarDespesa : AppCompatActivity() {

    private lateinit var binding: ActivityEditarDespesaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditarDespesaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idDespesa = intent.getStringExtra("idDespesa")
        val titulo = intent.getStringExtra("titulo")
        val valor = intent.getDoubleExtra("valor", 0.0)
        val dataPagamento = intent.getStringExtra("dataPagamento")

        titulo?.let {
            binding.inputTituloEditDespesa.setText(it)
        } ?: run {
            Log.e("EditarDespesa", "Nome não recebido!")
        }

        val valorFormatado = formatarValor(valor)
        binding.inputValorEditDespesa.setText(valorFormatado)
        binding.inputDataFinal.addTextChangedListener(DateTextWatcher(binding.inputDataFinal))
        binding.inputDataFinal.setText(dataPagamento)

        binding.ButtonEditdespesa.setOnClickListener {
            val novoTitulo = binding.inputTituloEditDespesa.text.toString()
            val novoValorFormatado = binding.inputValorEditDespesa.text.toString()
                .replace("R$", "") // Remove o "R$"
                .replace(".", "") // Remove o ponto separador de milhar
                .replace(",", ".") // Substitui a vírgula por ponto
                .trim()
            val novoValor = novoValorFormatado.toDoubleOrNull()
            val novaData = binding.inputDataFinal.text.toString()

            println(novoTitulo)
            println(novoValorFormatado)
            println(novaData)

            if (novoTitulo.isNotEmpty() && novoValor != null && novaData.isNotEmpty() && idDespesa != null) {
                val despesaAtualizada = Despesas(
                    id = idDespesa,
                    nome = novoTitulo,
                    valor = novoValor,
                    despesaFixa = binding.switch1.isChecked,
                    dataPagamento = novaData,
                    parcelas = 1
                )

                val despesasDAO = DespesasDAO(FirebaseFirestore.getInstance())
                val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (idUsuario.isNotEmpty()) {
                    despesasDAO.editarDespesa(
                        idUsuario,
                        despesaAtualizada,
                        onSuccess = {
                            Toast.makeText(this, "Despesa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                            finish()
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
    
    private fun formatarValor(valor: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return numberFormat.format(valor)
    }
    
    fun irParaDash(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    
    class DateTextWatcher(private val editText: EditText?) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable?) {
            editText?.removeTextChangedListener(this)

            val cleanString = editable.toString().replace(Regex("[^\\d]"), "")

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



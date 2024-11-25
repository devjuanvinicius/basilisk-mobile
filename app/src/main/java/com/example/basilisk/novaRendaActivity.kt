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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.databinding.ActivityNovaRendaBinding
import com.example.basilisk.model.Renda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.util.Locale
import java.util.UUID

class novaRendaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNovaRendaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o View Binding
        binding = ActivityNovaRendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val rendasDAO = RendaDAO(db)

        // Configura o TextWatcher para o campo de valor
        binding.inputValorRenda.addTextChangedListener(ExampleTextWatcher(binding.inputValorRenda))
        binding.inputDataFinal.addTextChangedListener(EditarDespesa.DateTextWatcher(binding.inputDataFinal))

        // Evento de clique do botão adicionar
        binding.buttonAddRenda.setOnClickListener {
            val titulo = binding.inputTituloRenda.text.toString()
            val valorText = binding.inputValorRenda.text.toString()
                .replace("[R$\\s,.]".toRegex(), "") // Remove caracteres como "R$", espaços e vírgulas
            val valor = valorText.toDoubleOrNull()?.div(100)
            val dataRecebimento = binding.inputDataFinal.text.toString()

            if (titulo.isNotEmpty() && valor != null && dataRecebimento.isNotEmpty()) {
                val rendaFixa = binding.switch1.isChecked
                val idAleatorio = UUID.randomUUID().toString()

                val novaRenda = Renda(
                    id = idAleatorio,
                    nome = titulo,
                    valor = valor,
                    rendaFixa = rendaFixa,
                    dataRecebimento = dataRecebimento
                )

                val idUsuario = auth.currentUser?.uid ?: ""
                if (idUsuario.isNotEmpty()) {
                    rendasDAO.criarRenda(
                        idUsuario,
                        novaRenda,
                        onSuccess = {
                            Toast.makeText(
                                this,
                                "Renda adicionada com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish() // Fecha a Activity
                        },
                        onFailure = { exception ->
                            Toast.makeText(
                                this,
                                "Erro ao adicionar renda: ${exception.message}",
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

    // Classe TextWatcher para o campo de valor
    class ExampleTextWatcher(private val editText: EditText?) : TextWatcher {

        companion object {
            private const val replaceRegex: String = "[R$,.\u00a0]"
            private const val replaceFinal: String = "R$\u00a0"
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(editable: Editable?) {
            try {
                val stringEditable = editable.toString()
                if (stringEditable.isEmpty()) return

                editText?.removeTextChangedListener(this)
                val cleanString = stringEditable.replace(replaceRegex.toRegex(), "")

                val parsed = BigDecimal(cleanString)
                    .setScale(2, BigDecimal.ROUND_FLOOR)
                    .divide(BigDecimal(100), BigDecimal.ROUND_FLOOR)

                val decimalFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR")) as DecimalFormat
                val formatted = decimalFormat.format(parsed)

                val stringFinal = formatted.replace(replaceFinal, "")
                editText?.setText(stringFinal)
                editText?.setSelection(stringFinal.length)
                editText?.addTextChangedListener(this)

            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }
}

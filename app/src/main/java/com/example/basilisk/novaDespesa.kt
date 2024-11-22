package com.example.basilisk

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.model.Despesas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.math.BigDecimal
import java.util.Locale

class novaDespesa : AppCompatActivity() {

    private lateinit var tituloInput: EditText
    private lateinit var valorInput: EditText
    private lateinit var dataPagamentoInput: EditText
    private lateinit var despesaFixaSwitch: SwitchCompat
    private lateinit var adicionarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nova_despesa)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tituloInput = findViewById(R.id.inputTituloDespesa)
        valorInput = findViewById(R.id.inputValorDespesa)
        dataPagamentoInput = findViewById(R.id.inputDataFinal)
        despesaFixaSwitch = findViewById(R.id.switch1)
        adicionarButton = findViewById(R.id.button3)
        valorInput.addTextChangedListener(ExampleTextWatcher(valorInput))

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val despesasDAO = DespesasDAO(db, auth)

        adicionarButton.setOnClickListener {
            val titulo = tituloInput.text.toString()
            val valorText = valorInput.text.toString()
                .replace("[R$\\s,.]".toRegex(), "") // Remove caracteres como "R$", espaços e vírgulas
            val valor = valorText.toDoubleOrNull()?.div(100)
            val dataPagamento = dataPagamentoInput.text.toString()

            if (titulo.isNotEmpty() && valor != null && dataPagamento.isNotEmpty()) {
                val despesaFixa = despesaFixaSwitch.isChecked

                val novaDespesa = Despesas(
                    id = "",
                    nome = titulo,
                    valor = valor,
                    despesaFixa = despesaFixa,
                    dataPagamento = dataPagamento,
                    tag = "default",
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

    class ExampleTextWatcher(val editText: EditText?) : TextWatcher {

        companion object {
            private const val replaceRegex: String = "[R$,.\u00a0]"
            private const val replaceFinal: String = "R$\u00a0"
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

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
                val formated = decimalFormat.format(parsed)

                val stringFinal = formated.replace(replaceFinal, "")
                editText?.setText(stringFinal)
                editText?.setSelection(stringFinal.length)
                editText?.addTextChangedListener(this)

            } catch (e: Exception){
                Log.e("ERROR", e.toString())
            }
        }

    }
    fun irParaDash(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }
}


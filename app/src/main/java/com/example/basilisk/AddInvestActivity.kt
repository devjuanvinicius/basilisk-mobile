package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.InvestimentoDAO
import com.example.basilisk.model.Investimento
import com.example.basilisk.utils.exibirMensagem
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class AddInvestActivity : AppCompatActivity() {

    private lateinit var inputCodigoAcao: EditText
    private lateinit var inputValorInvest: EditText
    private lateinit var inputQuantInvest: EditText
    private lateinit var inputDataInvest: EditText
    private lateinit var btnSalvarInvestimento: Button

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_invest)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputCodigoAcao = findViewById(R.id.inputCodigoAcao)
        inputValorInvest = findViewById(R.id.inputValorInvest)
        inputQuantInvest = findViewById(R.id.inputQuantInvest)
        inputDataInvest = findViewById(R.id.inputDataInvest)
        btnSalvarInvestimento = findViewById(R.id.buttonAddInvest)

        btnSalvarInvestimento.setOnClickListener {
            salvarInvestimento()
        }
    }

    private fun salvarInvestimento() {
        val codigoAcao = inputCodigoAcao.text.toString()
        val valorInvest = inputValorInvest.text.toString()
        val quantInvest = inputQuantInvest.text.toString()
        val dataInvest = inputDataInvest.text.toString()

        if (codigoAcao.isEmpty() || valorInvest.isEmpty() || quantInvest.isEmpty() || dataInvest.isEmpty()) {
            return
        }
        val investimento = Investimento(codigoAcao, dataInvest, quantInvest.toInt(), valorInvest.toDouble())

        InvestimentoDAO(db).criarInvestimento(
            auth.currentUser!!.uid,
            investimento,
            onSuccess = {
                irParaInvest()
            },
            onFailure = { exception ->
                val mensagemErro = getMensagemErro(exception)
                exibirMensagem(mensagemErro)
            }

        )

        irParaInvest()
    }

    public fun getMensagemErro(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
            is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
            is FirebaseNetworkException -> "Sem conexão com internet"
            else -> {
                Log.e("CadastroActivity", "Erro ao cadastrar: ${exception.message}", exception)
                "Erro ao cadastrar: ${exception.message}"
            }
        }
    }
    private fun irParaInvest() {
        val intent = Intent(this, InvestimentoActivity::class.java)
        startActivity(intent)
    }
}

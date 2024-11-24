package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddInvestActivity : AppCompatActivity() {

    private lateinit var inputCodigoAcao: EditText
    private lateinit var inputValorInvest: EditText
    private lateinit var inputQuantInvest: EditText
    private lateinit var inputDataInvest: EditText
    private lateinit var btnSalvarInvestimento: Button

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

        irParaInvest()
    }

    private fun irParaInvest() {
        val intent = Intent(this, InvestimentoActivity::class.java)
        startActivity(intent)
    }
}

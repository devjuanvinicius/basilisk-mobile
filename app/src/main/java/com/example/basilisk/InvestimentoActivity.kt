package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.database.InvestimentoDAO
import com.example.basilisk.model.Investimento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvestimentoActivity : AppCompatActivity() {

  lateinit var homeButton: ImageButton
  private lateinit var ibovespaTextView: TextView
  private lateinit var variaçãoTextView: TextView
  private lateinit var codigoAcaoEditText: EditText
  private lateinit var dataCompraEditText: EditText
  private lateinit var qtdAcoesEditText: EditText
  private lateinit var valorEditText: EditText

  private val auth by lazy { FirebaseAuth.getInstance() }
  private val db by lazy { FirebaseFirestore.getInstance() }
  private val investimentoDAO by lazy { InvestimentoDAO(db) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContentView(R.layout.activity_investimento)

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    homeButton = findViewById(R.id.homeButton)
    homeButton.setOnClickListener {
      val intent = Intent(this, MainActivity::class.java)
      startActivity(intent)
    }

    codigoAcaoEditText = findViewById(R.id.codigoAcaoEditText)
    dataCompraEditText = findViewById(R.id.dataCompraEditText)
    qtdAcoesEditText = findViewById(R.id.qtdAcoesEditText)
    valorEditText = findViewById(R.id.valorEditText)

    ibovespaTextView = findViewById(R.id.ibovespa)
    variaçãoTextView = findViewById(R.id.negativoVermelho)

    val token = "6AfdujsFQpyPMnwfgeNWFf"
    getIbovespa(token)
  }

  private fun getIbovespa(token: String) {
    val call = RetrofitClient.api.getIbovespa(token)
    call.enqueue(object : Callback<IbovespaResponse> {
      override fun onResponse(
        call: Call<IbovespaResponse>,
        response: Response<IbovespaResponse>
      ) {
        if (response.isSuccessful) {
          val ibovespa = response.body()?.results?.firstOrNull()
          ibovespa?.let {
            ibovespaTextView.text = "R$ ${it.regularMarketPrice}"
            variaçãoTextView.text = "${it.regularMarketChangePercent}%"
            if (it.regularMarketChangePercent < 0) {
              variaçãoTextView.setTextColor(resources.getColor(R.color.red))
            } else {
              variaçãoTextView.setTextColor(resources.getColor(R.color.white))
            }
          }
        } else {
          ibovespaTextView.text = "Erro ao buscar dados"
          variaçãoTextView.text = "Erro na variação"
        }
      }

      override fun onFailure(call: Call<IbovespaResponse>, t: Throwable) {
        ibovespaTextView.text = "Erro de conexão"
        variaçãoTextView.text = "Erro na variação"
      }
    })
  }

  fun adicionarInvestimento(view: View) {
    val idUsuario = auth.currentUser?.uid ?: return
    val codigoAcao = codigoAcaoEditText.text.toString()
    val dataCompra = dataCompraEditText.text.toString()
    val qtdAcoes = qtdAcoesEditText.text.toString().toIntOrNull()
    val valor = valorEditText.text.toString().toDoubleOrNull()

    if (codigoAcao.isEmpty() || dataCompra.isEmpty() || qtdAcoes == null || valor == null) {
      Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
      return
    }

    val investimento = Investimento(
      codigoAcao = codigoAcao,
      dataCompra = dataCompra,
      qtdAcoes = qtdAcoes,
      valor = valor
    )

    investimentoDAO.criarInvestimento(idUsuario, investimento,
      onSuccess = {
        Toast.makeText(this, "Investimento cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
        limparCampos()
      },
      onFailure = { exception ->
        Toast.makeText(this, "Erro ao cadastrar investimento: ${exception.message}", Toast.LENGTH_SHORT).show()
      })
  }

  private fun limparCampos() {
    codigoAcaoEditText.text.clear()
    dataCompraEditText.text.clear()
    qtdAcoesEditText.text.clear()
    valorEditText.text.clear()
  }

  fun irParaAddInvest(view: View) {
    val intent = Intent(view.context, AddInvestActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaHome(view: View) {
    val intent = Intent(view.context, MainActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaCofrinho(view: View) {
    val intent = Intent(view.context, CofrinhoActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaPerfil(view: View) {
    val intent = Intent(view.context, PerfilActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaCalendario(view: View) {
    val intent = Intent(view.context, CalendarioActivity::class.java)
    view.context.startActivity(intent)
  }
}

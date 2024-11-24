package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.model.Investimento
import com.example.basilisk.network.IbovespaResponse
import com.example.basilisk.network.RetrofitClient
import com.example.basilisk.recyclers.ItemAdapterInvestimento
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class InvestimentoActivity : AppCompatActivity() {

  lateinit var homeButton: ImageButton
  private lateinit var ibovespaTextView: TextView
  private lateinit var variaçãoTextView: TextView
  private lateinit var rvInvestimento: RecyclerView

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

    ibovespaTextView = findViewById(R.id.ibovespa)
    variaçãoTextView = findViewById(R.id.negativoVermelho)


    val token = "6AfdujsFQpyPMnwfgeNWFf"
    getIbovespa(token)

    val investimentos = listOf(
      Investimento(codigoAcao = "PETR4", dataCompra = "2024-01-15", qtdAcoes = 100, valor = 28.50),
      Investimento(codigoAcao = "VALE3", dataCompra = "2023-12-10", qtdAcoes = 50, valor = 70.20),
      Investimento(codigoAcao = "ITUB4", dataCompra = "2024-02-01", qtdAcoes = 200, valor = 24.90),
      Investimento(codigoAcao = "ABEV3", dataCompra = "2023-11-20", qtdAcoes = 150, valor = 14.75),
      Investimento(codigoAcao = "BBDC4", dataCompra = "2024-03-05", qtdAcoes = 80, valor = 16.60)
    )


    rvInvestimento = findViewById(R.id.rvInvestimento)
    rvInvestimento.adapter = ItemAdapterInvestimento(investimentos)
    rvInvestimento.layoutManager = LinearLayoutManager(this)

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


  fun irParaAddInvest(view: View) {
    val intent = Intent(view.context, AddInvestActivity::class.java)
    view.context.startActivity(intent)
  }

  fun irParaEditInvest(view: View) {
    val intent = Intent(view.context, EditInvestActivity::class.java)
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

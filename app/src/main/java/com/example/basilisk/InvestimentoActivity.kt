package com.example.basilisk

import android.content.Intent
import android.icu.text.NumberFormat
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
import com.example.basilisk.database.InvestimentoDAO
import com.example.basilisk.model.Investimento
import com.example.basilisk.network.ApiResponse
import com.example.basilisk.network.RetrofitClient
import com.example.basilisk.recyclers.ItemAdapterInvestimento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class InvestimentoActivity : AppCompatActivity() {

  lateinit var homeButton: ImageButton
  private lateinit var ibovespaTextView: TextView
  private lateinit var variaçãoTextView: TextView
  private lateinit var rvInvestimento: RecyclerView
  private lateinit var principalTextView: TextView
  private val db by lazy { FirebaseFirestore.getInstance() }
  private val auth by lazy { FirebaseAuth.getInstance() }

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

    variaçãoTextView = findViewById(R.id.negativoVermelho)

    val token = "6AfdujsFQpyPMnwfgeNWFf"
    getIbovespa(token)

    var investimentos: List<Investimento>

    InvestimentoDAO(db).retornarInvestimento(
      auth.currentUser!!.uid,
      onSuccess = { investimentosList ->
        investimentos = investimentosList

        rvInvestimento = findViewById(R.id.rvInvestimento)
        rvInvestimento.adapter = ItemAdapterInvestimento(investimentos)
        rvInvestimento.layoutManager = LinearLayoutManager(this)

        carregadorDado(investimentos)
      },
      onFailure = {exception -> val ficaaqui = exception}
    )
  }

  override fun onResume() {
    super.onResume()
    var investimentos: List<Investimento>

    InvestimentoDAO(db).retornarInvestimento(
      auth.currentUser!!.uid,
      onSuccess = { investimentosList ->
        investimentos = investimentosList

        rvInvestimento = findViewById(R.id.rvInvestimento)
        rvInvestimento.adapter = ItemAdapterInvestimento(investimentosList)
        rvInvestimento.layoutManager = LinearLayoutManager(this)

        carregadorDado(investimentos)
      },
      onFailure = { exception ->

        exception.printStackTrace()
      }
    )
  }

  private fun carregadorDado(investimentosList: List<Investimento>) {
    principalTextView = findViewById(R.id.principalAtivoText)

    // Atualizar o texto com o nome da primeira ação
    if (investimentosList.isNotEmpty()) {
      principalTextView.text = investimentosList[0].nomeAcao
    } else {
      principalTextView.text = "Nenhum investimento encontrado"
    }
  }

  private fun getIbovespa(token: String) {
    val formatador = NumberFormat.getPercentInstance(Locale("pt", "BR")).apply {
      minimumFractionDigits = 2
    }

    val call = RetrofitClient.api.getAcao("^BVSP", token)
    call.enqueue(object : Callback<ApiResponse> {
      override fun onResponse(
        call: Call<ApiResponse>,
        response: Response<ApiResponse>
      ) {
        if (response.isSuccessful) {
          val ibovespa = response.body()?.results?.firstOrNull()
          ibovespa?.let {
            variaçãoTextView.text = formatador.format(it.regularMarketChangePercent)
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

      override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
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

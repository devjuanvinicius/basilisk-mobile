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
import com.example.basilisk.network.IbovespaResponse
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
      },
      onFailure = {exception -> val ficaaqui = exception}
    )
  }

  private fun getIbovespa(token: String) {
    val formatador = NumberFormat.getPercentInstance(Locale("pt", "BR")).apply {
      minimumFractionDigits = 2
    }

    val call = RetrofitClient.api.getIbovespa(token)
    call.enqueue(object : Callback<IbovespaResponse> {
      override fun onResponse(
        call: Call<IbovespaResponse>,
        response: Response<IbovespaResponse>
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

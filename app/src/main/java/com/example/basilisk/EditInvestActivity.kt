package com.example.basilisk

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.basilisk.EditarDespesa.DateTextWatcher
import com.example.basilisk.database.InvestimentoDAO
import com.example.basilisk.databinding.ActivityEditInvestBinding
import com.example.basilisk.model.Investimento
import com.example.basilisk.network.ApiResponse
import com.example.basilisk.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class EditInvestActivity : AppCompatActivity() {

  private lateinit var binding: ActivityEditInvestBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Inicialização do View Binding
    binding = ActivityEditInvestBinding.inflate(layoutInflater)
    setContentView(binding.root)

    enableEdgeToEdge()

    // Ajuste do padding para compatibilidade com as barras do sistema
    ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
      insets
    }

    // Recuperando dados da Intent
    val codigoInvestimento = intent.getStringExtra("codigoInvestimento")
    val valor = intent.getDoubleExtra("valor", 0.0)
    val nomeAcao = intent.getStringExtra("nomeAcao")
    val dataCompra = intent.getStringExtra("dataCompra")
    val qtdAcoes = intent.getIntExtra("qtdAcoes", 0)

    // Atualizando o título com o nome da ação
    val tituloAtual = binding.textEditInvest.text.toString()
    val tituloComAcao = "$tituloAtual $nomeAcao"
    binding.textEditInvest.text = tituloComAcao

    // Formatando o valor e preenchendo os campos
    val valorFormatado = formatarValor(valor)
    binding.inputCodigoAcao.setText(codigoInvestimento)
    binding.inputValorInvest.setText(valorFormatado)
    binding.inputDataInvest.addTextChangedListener(DateTextWatcher(binding.inputDataInvest))
    binding.inputDataInvest.setText(dataCompra)
    binding.inputQuantInvest.setText(qtdAcoes.toString())

    binding.buttonEditInvest.setOnClickListener {
      atualizarInvestimento(codigoInvestimento!!)
    }
  }

  private fun atualizarInvestimento(codigoAcaoAntigo: String) {
    val novoCodigo = binding.inputCodigoAcao.text.toString()
    val novoValorFormatado = binding.inputValorInvest.text.toString()
      .replace("R$", "") // Remove o "R$"
      .replace(".", "") // Remove o ponto separador de milhar
      .replace(",", ".") // Substitui a vírgula por ponto
      .trim()
    val novoValor = novoValorFormatado.toDoubleOrNull()
    val novaData = binding.inputDataInvest.text.toString()
    val novaQtdAcoes = binding.inputQuantInvest.text.toString().toIntOrNull()

    val token = "6AfdujsFQpyPMnwfgeNWFf"
    val call = RetrofitClient.api.getAcao(novoCodigo, token)

    call.enqueue(object : Callback<ApiResponse> {
      override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
        if (response.isSuccessful) {
          val result = response.body()?.results?.firstOrNull()
          val nomeAcao = result?.shortName

          if (nomeAcao != null && novoCodigo.isNotEmpty() && novoValor != null && novaData.isNotEmpty() && novaQtdAcoes != null) {
            val investimentoAtualizado = Investimento(
              codigoAcao = novoCodigo,
              dataCompra = novaData,
              qtdAcoes = novaQtdAcoes,
              valor = novoValor,
              nomeAcao = nomeAcao
            )

            val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            if (idUsuario.isNotEmpty()) {
              InvestimentoDAO(FirebaseFirestore.getInstance()).editarInvestimento(
                idUsuario,
                investimentoAtualizado,
                codigoAcaoAntigo,
                onSuccess = {
                  Toast.makeText(this@EditInvestActivity, "Investimento atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                  finish()
                },
                onFailure = { exception ->
                  Toast.makeText(this@EditInvestActivity, "Erro ao atualizar investimento: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
              )
            } else {
              Toast.makeText(this@EditInvestActivity, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
            }
          } else {
            Toast.makeText(this@EditInvestActivity, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show()
          }
        } else {
          Toast.makeText(this@EditInvestActivity, "Erro ao buscar dados da ação!", Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        Toast.makeText(this@EditInvestActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
        t.printStackTrace()
      }
    })
  }

  private fun formatarValor(valor: Double): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return numberFormat.format(valor)
  }

  // Função para navegação
  fun irParaInvest(view: View) {
    val intent = Intent(view.context, InvestimentoActivity::class.java)
    view.context.startActivity(intent)
  }
}

package com.example.basilisk

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.model.Despesas
import com.example.basilisk.recyclers.ItemAdapterDespesa
import com.example.basilisk.utils.exibirMensagem
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class CalendarioActivity : AppCompatActivity() {
    private lateinit var rvLista: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var editarPagamento: TextView
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var despesasList: List<Despesas> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendario)

        editarPagamento = findViewById(R.id.EditarEconomia)

        rvLista = findViewById(R.id.rv_calendario)
        calendarView = findViewById(R.id.calendarView)
        rvLista.layoutManager = LinearLayoutManager(this)

        recuperarDespesas { despesas ->
            despesasList = despesas
            atualizarRecyclerView(despesasList)
            atualizarCalendarView(despesasList)
            atualizarDadosTela(despesasList)
        }

        editarPagamento.setOnClickListener {
            val intent = Intent(this, EditarDespesa::class.java)
            startActivity(intent)
        }
    }

    private fun atualizarDadosTela(despesas: List<Despesas>) {
        val valorDespesaTela: TextView = findViewById(R.id.valorProximoPagamento)
        val dataDespesaTela: TextView = findViewById(R.id.textDataPagamento)

        val valorDespesa: Double = despesas[0].valor
        val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        valorDespesaTela.text = formatador.format(valorDespesa)
        dataDespesaTela.text = despesas[0].dataPagamento
    }

    private fun recuperarDespesas(onComplete: (List<Despesas>) -> Unit) {
        val idUsuario = auth.currentUser?.uid
        if (idUsuario != null) {
            DespesasDAO(db).retornarDespesa(
                idUsuario,
                onSuccess = { despesas ->
                    onComplete(despesas)
                },
                onFailure = { exception ->
                    val mensagemErro = getMensagemErro(exception)
                    exibirMensagem(mensagemErro)
                    onComplete(emptyList())
                }
            )
        } else {
            exibirMensagem("Usuário não autenticado.")
            onComplete(emptyList())
        }
    }


    private fun atualizarRecyclerView(despesas: List<Despesas>) {
        println(despesas[0])
        rvLista.adapter = ItemAdapterDespesa(
            despesas,
            onDeleteClick = {}
        )
    }

    private fun atualizarCalendarView(despesas: List<Despesas>) {
        if (despesas.isNotEmpty()) {
            val despesasDatas = despesas.mapNotNull { stringToCalendar(it.dataPagamento) }
            val primeiraDataArray = despesasDatas.first()
            primeiraDataArray.add(Calendar.MONTH, 1)

            calendarView.setDate(primeiraDataArray.timeInMillis)
        }
    }

    private fun stringToCalendar(dateString: String): Calendar? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Altere para dd/MM/yyyy
            val date = format.parse(dateString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
            calendar
        } catch (e: Exception) {
            Log.e("CalendarioActivity", "Erro ao converter data: $dateString", e)
            null
        }
    }

    private fun getMensagemErro(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthWeakPasswordException -> "Digite uma senha com no mínimo 6 caracteres"
            is FirebaseAuthInvalidCredentialsException -> "Digite um e-mail válido"
            is FirebaseAuthUserCollisionException -> "E-mail já cadastrado"
            is FirebaseNetworkException -> "Sem conexão com internet"
            else -> {
                Log.e("CalendarioActivity", "Erro ao carregar: ${exception.message}", exception)
                "Erro ao carregar: ${exception.message}"
            }
        }
    }

    fun irParaInvestimento(view: View) {
        val intent = Intent(view.context, InvestimentoActivity::class.java)
        view.context.startActivity(intent)
    }
    fun irParaCoffin(view: View) {
        val intent = Intent(view.context, CofrinhoActivity::class.java)
        view.context.startActivity(intent)
    }
    fun irParaAddRenda(view: View) {
        val intent = Intent(view.context, novaRendaActivity::class.java)
        view.context.startActivity(intent)
    }
    fun irParaPerfil(view: View) {
        val intent = Intent(view.context, PerfilActivity::class.java)
        view.context.startActivity(intent)
    }
    fun irParaMain(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }
}
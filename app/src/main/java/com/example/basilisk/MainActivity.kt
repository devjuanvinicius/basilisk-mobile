package com.example.basilisk

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.databinding.ActivityMainBinding
import com.example.basilisk.model.Despesas
import com.example.basilisk.model.Renda
import com.example.basilisk.recyclers.ItemAdapterDespesa
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pieChart: PieChart
    private lateinit var rvLista: RecyclerView

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val customFont by lazy { Typeface.createFromAsset(assets, "fonts/custom_font.ttf") }
    private val formatador by lazy { NumberFormat.getCurrencyInstance(Locale("pt", "BR")) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupSpinner()
        setupPieChart()
        setupButtons()

        rvLista = binding.rvDashboard
        rvLista.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        val despesasArray = mutableListOf<Despesas>()
        loadDespesas(despesasArray)
        calculaSaldo(despesasArray)
    }


    private fun calculaSaldo(despesasArray: List<Despesas>) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("Erro", "Usuário não autenticado")
            return
        }

        RendaDAO(db).retornarRenda(
            currentUser.uid,
            onSuccess = { rendas ->
                val totalRenda = calcularTotalRenda(rendas)
                val totalDespesa = calcularTotalDespesa(despesasArray)

                val total = totalRenda - totalDespesa
                val textView: TextView = binding.saldoText
                textView.text = formatador.format(total)
            },
            onFailure = { exception ->
                Log.e("Erro", "Falha ao carregar rendas: ${exception.message}")
            }
        )
    }

    private fun calcularTotalDespesa(despesasArray: List<Despesas>): Double {
        return despesasArray.sumOf { it.valor }
    }

    private fun calcularTotalRenda(rendaLista: List<Renda>): Double {
        return rendaLista.sumOf { it.valor }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSpinner() {
        val spinner: Spinner = binding.spinnermes
        val meses = resources.getStringArray(R.array.meses)
        val mesesAbrev = arrayOf("Nov", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val selectedMonthTextView: TextView = binding.selectedMonth
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMonthTextView.text = mesesAbrev[position]
                selectedMonthTextView.typeface = customFont
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupPieChart() {
        pieChart = binding.pieChart

        val gastos = 600f
        val renda = 1000f
        val porcentagemGastos = (gastos / renda) * 100

        val entries = arrayListOf(
            PieEntry(gastos, "Gasto"),
            PieEntry(renda - gastos, "Renda")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.parseColor("#FFBF54"), Color.BLACK)
        dataSet.setDrawValues(false)

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.setDrawEntryLabels(false)
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 58f
        pieChart.setHoleColor(Color.parseColor("#1C1C1C"))
        pieChart.setCenterText("${porcentagemGastos.toInt()}%")
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.setCenterTextTypeface(customFont)

        pieChart.description.isEnabled = false

        val legend = pieChart.legend
        legend.isEnabled = true
        legend.textColor = Color.WHITE
        legend.textSize = 12f
        legend.typeface = customFont
    }

    private fun setupButtons() {
        binding.buttonTesteDeLogof.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, EntryActivity::class.java)
            startActivity(intent)
        }

        val buttonRenda = binding.buttonRenda
        val buttonDespesa = binding.buttonDespesa

        if (supportFragmentManager.fragments.isEmpty()) {
            replaceFragment(RendaFragment())
        }

        buttonRenda.setOnClickListener { replaceFragment(DespesaFragment()) }
        buttonDespesa.setOnClickListener { replaceFragment(RendaFragment()) }
    }

    private fun loadDespesas(despesasArray: MutableList<Despesas>) {
        val currentUser = auth.currentUser
        if (currentUser?.uid != null) {
            DespesasDAO(db).retornarDespesa(
                currentUser.uid,
                onSuccess = { despesasList ->
                    despesasArray.clear() // Limpar a lista antes de adicionar os novos dados
                    despesasArray.addAll(despesasList) // Adiciona as despesas recebidas

                    rvLista.adapter = ItemAdapterDespesa(
                        despesasArray,
                        onDeleteClick = { despesaId -> excluirDespesa(despesaId, despesasArray) }
                    )
                    atualizarDadosNaTela(despesasArray)
                },
                onFailure = { }
            )
        }
    }

    private fun excluirDespesa(despesaId: String, despesasArray: MutableList<Despesas>) {
        DespesasDAO(db).deletarDespesa(
            auth.currentUser!!.uid, despesaId,
            onSuccess = { atualizarDadosNaTela(despesasArray) },
            onFailure = {}
        )
    }

    private fun atualizarDadosNaTela(despesasArray: MutableList<Despesas>) {
        val diaPagamentoText: TextView = binding.numberBackground
        val proxPagamentoText: TextView = binding.nomeProximoPagamento
        val valorProxPagamentoText: TextView = binding.valorProximoPagamento

        if (despesasArray.isNotEmpty()) {
            val primeiraDespesa = despesasArray[0]
            val diaPagamento = primeiraDespesa.dataPagamento.substring(0, 2)

            diaPagamentoText.text = diaPagamento
            proxPagamentoText.text = primeiraDespesa.nome
            valorProxPagamentoText.text = formatador.format(primeiraDespesa.valor)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null) // Adiciona o fragmento à pilha de navegação
            .commit()
    }

    fun irParaInvestimento(view: View) = startActivity(Intent(this, InvestimentoActivity::class.java))
    fun irParaCoffin(view: View) = startActivity(Intent(this, CofrinhoActivity::class.java))
    fun irParaAddRenda(view: View) = startActivity(Intent(this, novaRendaActivity::class.java))
    fun irParaPerfil(view: View) = startActivity(Intent(this, PerfilActivity::class.java))
    fun irParaCalendario(view: View) = startActivity(Intent(this, CalendarioActivity::class.java))
}

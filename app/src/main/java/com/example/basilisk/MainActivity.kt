package com.example.basilisk

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupSpinner()
        setupPieChart()
        setupButtons()
        loadDespesas()

        rvLista = findViewById(R.id.rv_dashboard)
        rvLista.layoutManager = LinearLayoutManager(this)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSpinner() {
        val spinner: Spinner = findViewById(R.id.spinnermes)
        val meses = resources.getStringArray(R.array.meses)
        val mesesAbrev = arrayOf("Nov", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")
        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val selectedMonthTextView: TextView = findViewById(R.id.selectedMonth)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMonthTextView.text = mesesAbrev[position]
                selectedMonthTextView.typeface = customFont
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupPieChart() {
        pieChart = findViewById(R.id.pieChart)

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

        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")
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

        val buttonRenda = findViewById<Button>(R.id.buttonRenda)
        val buttonDespesa = findViewById<Button>(R.id.buttonDespesa)

        if (supportFragmentManager.fragments.isEmpty()) {
            replaceFragment(RendaFragment())
        }

        buttonRenda.setOnClickListener { replaceFragment(DespesaFragment()) }
        buttonDespesa.setOnClickListener { replaceFragment(RendaFragment()) }
    }

    private fun loadDespesas() {
        val despesasArray = mutableListOf<Despesas>()

        if (auth.currentUser?.uid != null) {
            DespesasDAO(db).retornarDespesa(
                auth.currentUser!!.uid,
                onSuccess = { despesasList ->
                    despesasArray.clear() // Limpar a lista antes de adicionar os novos dados
                    despesasArray.addAll(despesasList) // Adiciona as despesas recebidas

                    rvLista.adapter = ItemAdapterDespesa(
                        despesasArray,
                        onDeleteClick = { despesaId ->
                            excluirDespesa(despesaId, despesasArray)
                        }
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
        val diaPagamentoText: TextView = findViewById(R.id.numberBackground)
        val proxPagamentoText: TextView = findViewById(R.id.nomeProximoPagamento)
        val valorProxPagamentoText: TextView = findViewById(R.id.valorProximoPagamento)

        val primeiraDespesa = despesasArray[0]
        val diaPagamento = primeiraDespesa.dataPagamento.substring(0,2)

        val formater = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        diaPagamentoText.text = diaPagamento
        proxPagamentoText.text = primeiraDespesa.nome
        valorProxPagamentoText.text = formater.format(primeiraDespesa.valor)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    fun irParaInvestimento(view: View) = startActivity(Intent(this, InvestimentoActivity::class.java))
    fun irParaCoffin(view: View) = startActivity(Intent(this, CofrinhoActivity::class.java))
    fun irParaAddRenda(view: View) = startActivity(Intent(this, novaRendaActivity::class.java))
    fun irParaPerfil(view: View) = startActivity(Intent(this, PerfilActivity::class.java))
    fun irParaCalendario(view: View) = startActivity(Intent(this, CalendarioActivity::class.java))
}




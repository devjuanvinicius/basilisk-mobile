package com.example.basilisk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basilisk.databinding.ActivityMainBinding
import com.example.basilisk.model.Despesas
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pieChart: PieChart
    private lateinit var rvLista: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")

        // Configurando o Spinner
        val spinner: Spinner = findViewById(R.id.spinnermes)
        val meses = resources.getStringArray(R.array.meses)
        val mesesabv = arrayOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val selectedMonthTextView: TextView = findViewById(R.id.selectedMonth)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMonthTextView.text = mesesabv[position]
                selectedMonthTextView.typeface = customFont
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.buttonTesteDeLogof.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val voltarTelaLogin = Intent(this, CadastroActivity::class.java)
            startActivity(voltarTelaLogin)
        }

        pieChart = findViewById(R.id.pieChart)

        val gastos = 600f
        val renda = 1000f
        val porcentagemGastos = (gastos / renda) * 100

        // Criando entradas para o gráfico
        val entries = arrayListOf(
            PieEntry(gastos, "Gasto"),
            PieEntry(renda - gastos, "Renda")
        )

        // Definindo as cores específicas
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.parseColor("#FFBF54"), Color.BLACK)
        dataSet.setDrawValues(false)

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.setDrawEntryLabels(false)
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 58f
        pieChart.setHoleColor(Color.parseColor("#1C1C1C"))

        // Aplicando a fonte customizada no texto central
        pieChart.setCenterText("${porcentagemGastos.toInt()}%")
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextColor(Color.WHITE)
        pieChart.setCenterTextTypeface(customFont) // Fonte customizada no texto central

        // Remover a descrição do gráfico
        pieChart.description.isEnabled = false

        // Customizando a legenda
        val legend = pieChart.legend
        legend.isEnabled = true
        legend.textColor = Color.WHITE
        legend.textSize = 12f
        legend.typeface = customFont // Aplicando fonte customizada na legenda
        val typeface = Typeface.createFromAsset(
            assets,
            "fonts/custom_font.ttf"
        ) // Certifique-se de que o caminho está correto
        pieChart.legend.typeface = typeface
        val boldTypeface = Typeface.create(typeface, Typeface.BOLD) // Define a fonte como bold

        // Aplicar a fonte bold à legenda e ao texto central
        pieChart.legend.typeface = boldTypeface
        pieChart.setCenterTextTypeface(boldTypeface)

        val buttonRenda = findViewById<Button>(R.id.buttonRenda)
        val buttonDespesa = findViewById<Button>(R.id.buttonDespesa)

        if (savedInstanceState == null) {
            replaceFragment(RendaFragment())
        }

        buttonRenda.setOnClickListener {
            replaceFragment(DespesaFragment())
        }

        buttonDespesa.setOnClickListener {
            replaceFragment(RendaFragment())
        }

        rvLista = findViewById(R.id.rv_dashboard)
        rvLista.adapter = ItemAdapter() //Aqui vc vai colocar a lista com as despesas
        rvLista.layoutManager = LinearLayoutManager(this)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment, fragment)
        fragmentTransaction.commit()
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
    fun irParaCalendario(view: View) {
        val intent = Intent(view.context, CalendarioActivity::class.java)
        view.context.startActivity(intent)
    }
}



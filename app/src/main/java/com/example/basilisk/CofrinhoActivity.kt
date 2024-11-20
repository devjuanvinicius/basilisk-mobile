package com.example.basilisk

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class CofrinhoActivity : AppCompatActivity() {
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cofrinho) // Certifique-se de que este layout existe
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pieChart = findViewById(R.id.pieChart)
        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")

        val gastos = 700f
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
    }

    fun irParaDash(view: View) {
        intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }

    fun irParaInvestimento(view: View) {
        intent = Intent(view.context, InvestimentoActivity::class.java)
        view.context.startActivity(intent)
    }

    fun irparaAddMeta(view: View) {
        intent = Intent(view.context, FragmentaAddMeta::class.java)
        view.context.startActivity(intent)
    }
}


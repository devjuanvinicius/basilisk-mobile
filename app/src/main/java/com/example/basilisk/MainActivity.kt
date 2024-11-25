package com.example.basilisk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basilisk.database.DespesasDAO
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.databinding.ActivityMainBinding
import com.example.basilisk.model.Usuario
import com.example.basilisk.recyclers.ItemAdapterDespesa
import com.example.basilisk.recyclers.ItemAdapterRendas
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pieChart: PieChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val idUsuario = auth.currentUser?.uid ?: ""
        val despesasDAO = DespesasDAO(db, auth)
        var usuario: Usuario


        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")

        // Configuração do Spinner
        val meses = resources.getStringArray(R.array.meses)
        val mesesabv = arrayOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnermes.adapter = adapter

        binding.spinnermes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                binding.selectedMonth.text = mesesabv[position]
                binding.selectedMonth.typeface = customFont
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Botão de logout
        binding.buttonTesteDeLogof.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, CadastroActivity::class.java))
            finish()
        }

        configurarPieChart(customFont)
        calculoSaldo()

        // Configurar RecyclerView para despesas
        despesasDAO.retornarDespesa(
            idUsuario = idUsuario,
            onSuccess = { despesasList ->
                // Configurar RecyclerView
                val recyclerView = binding.rvDashboard
                recyclerView.layoutManager = LinearLayoutManager(this)

                // Passando a função de exclusão para o adaptador
                recyclerView.adapter = ItemAdapterDespesa(despesasList.toMutableList()) { idDespesa ->
                    // Função de exclusão: chamando o método de deletar despesa no DAO
                    despesasDAO.deletarDespesa(
                        idUsuario = idUsuario,
                        idDespesas = idDespesa,
                        onSuccess = {
                            // Atualiza a RecyclerView após a exclusão
                            despesasDAO.retornarDespesa(idUsuario, { despesas ->
                                (binding.rvDashboard.adapter as ItemAdapterDespesa).updateList(despesas)

                            }, { exception ->
                                Log.e("MainActivity", "Erro ao atualizar lista de despesas: ${exception.message}")
                            })
                        },
                        onFailure = { exception ->
                            Log.e("MainActivity", "Erro ao deletar despesa: ${exception.message}")
                        }
                    )
                }
            },
            onFailure = { exception ->
                // Tratar erro
                Log.e("MainActivity", "Erro ao buscar despesas: ${exception.message}")
            }
        )

        despesasDAO.ouvirDespesas(
            idUsuario = idUsuario,
            onChange = { despesasList ->
                // Atualiza a RecyclerView automaticamente com as despesas em tempo real
                val recyclerView = binding.rvDashboard
                recyclerView.layoutManager = LinearLayoutManager(this)

                // Passando a função de exclusão para o adaptador
                recyclerView.adapter = ItemAdapterDespesa(despesasList.toMutableList()) { idDespesa ->
                    // Função de exclusão: chamando o método de deletar despesa no DAO
                    despesasDAO.deletarDespesa(
                        idUsuario = idUsuario,
                        idDespesas = idDespesa,
                        onSuccess = {
                            // Atualiza a RecyclerView após a exclusão
                            despesasDAO.retornarDespesa(idUsuario, { despesas ->
                                (binding.rvDashboard.adapter as ItemAdapterDespesa).updateList(despesas)
                            }, { exception ->
                                Log.e("MainActivity", "Erro ao atualizar lista de despesas: ${exception.message}")
                            })
                        },
                        onFailure = { exception ->
                            Log.e("MainActivity", "Erro ao deletar despesa: ${exception.message}")
                        }
                    )
                }
            },
            onFailure = { exception ->
                // Tratar erro
                Log.e("MainActivity", "Erro ao buscar despesas: ${exception.message}")
            }
        )







        // Configurar Fragment inicial
        if (savedInstanceState == null) {
            replaceFragment(RendaFragment())
        }

        // Configurar botões para troca de fragmentos
        binding.buttonRenda.setOnClickListener {
            replaceFragment(DespesaFragment())
        }

        binding.buttonDespesa.setOnClickListener {
            replaceFragment(RendaFragment())
        }
    }

    private fun calculoSaldo() {
        TODO("Not yet implemented")
    }

    private fun configurarPieChart(customFont: Typeface) {
        val gastos = 600f
        val renda = 1000f
        val porcentagemGastos = (gastos / renda) * 100

        pieChart = binding.pieChart

        val entries = arrayListOf(
            PieEntry(gastos, "Gasto"),
            PieEntry(renda - gastos, "Renda")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.parseColor("#FFBF54"), Color.BLACK)
            setDrawValues(false)
        }

        pieChart.apply {
            data = PieData(dataSet)
            setDrawEntryLabels(false)
            isDrawHoleEnabled = true
            holeRadius = 58f
            setHoleColor(Color.parseColor("#1C1C1C"))
            setCenterText("${porcentagemGastos.toInt()}%")
            setCenterTextSize(20f)
            setCenterTextColor(Color.WHITE)
            setCenterTextTypeface(customFont)
            description.isEnabled = false
            legend.apply {
                isEnabled = true
                textColor = Color.WHITE
                textSize = 12f
                typeface = Typeface.create(customFont, Typeface.BOLD)
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    // Funções para navegação entre Activities
    fun irParaInvestimento(view: View) {
        startActivity(Intent(view.context, InvestimentoActivity::class.java))
    }

    fun irParaCoffin(view: View) {
        startActivity(Intent(view.context, CofrinhoActivity::class.java))
    }

    fun irParaAddRenda(view: View) {
        startActivity(Intent(view.context, novaRendaActivity::class.java))
    }

    fun irParaPerfil(view: View) {
        startActivity(Intent(view.context, PerfilActivity::class.java))
    }

    fun irParaCalendario(view: View) {
        startActivity(Intent(view.context, CalendarioActivity::class.java))
    }
}




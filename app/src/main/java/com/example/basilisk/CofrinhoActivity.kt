package com.example.basilisk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.basilisk.databinding.ActivityCofrinhoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class CofrinhoActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var viewPagerEconomizado: ViewPager2
    private lateinit var viewPagerValorPorMes: ViewPager2
    private lateinit var viewPagerProgresso: ViewPager2
    private lateinit var viewPagerProgressBar: ViewPager2

    private lateinit var metasAdapter: MetasAdapter
    private lateinit var valorEconomizadoAdapter: ValorEconomizadoAdapter
    private lateinit var valorPorMesAdapter: ValorPorMesAdapter
    private lateinit var progressoAdapter: ProgressoAdapter
    private lateinit var progressBarAdapter: ProgressBarAdapter

    private lateinit var binding: ActivityCofrinhoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCofrinhoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando os ViewPager2 existentes
        viewPager = findViewById(R.id.viewPager)
        metasAdapter = MetasAdapter(this)
        viewPager.adapter = metasAdapter

        viewPagerEconomizado = findViewById(R.id.viewPagerEconomizado)
        valorEconomizadoAdapter = ValorEconomizadoAdapter(this)
        viewPagerEconomizado.adapter = valorEconomizadoAdapter

        viewPagerValorPorMes = findViewById(R.id.viewPagerValorPorMes)
        valorPorMesAdapter = ValorPorMesAdapter(this)
        viewPagerValorPorMes.adapter = valorPorMesAdapter

        viewPagerProgresso = findViewById(R.id.viewPagerProgresso)
        progressoAdapter = ProgressoAdapter(this, listOf())
        viewPagerProgresso.adapter = progressoAdapter

        // Inicializando o novo ViewPager2 para ProgressBars
        viewPagerProgressBar = findViewById(R.id.viewPagerProgressBar)
        progressBarAdapter = ProgressBarAdapter(this, listOf())
        viewPagerProgressBar.adapter = progressBarAdapter

        // Sincronizar o movimento dos ViewPager2
        val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPagerEconomizado.setCurrentItem(position, false)
                viewPagerValorPorMes.setCurrentItem(position, false)
                viewPagerProgresso.setCurrentItem(position, false)
                viewPagerProgressBar.setCurrentItem(position, false) // Novo ViewPager sincronizado
            }
        }
        viewPager.registerOnPageChangeCallback(pageChangeCallback)

        // Botão de nova economia
        binding.NovaEconomia.setOnClickListener {
            Log.d("CofrinhoActivity", "Navegando para AddMeta")
            intent = Intent(this, AddMeta::class.java)
            startActivity(intent)
        }

        // Botão de editar economia (agora redireciona para EditMeta)
        binding.EditarEconomia.setOnClickListener {
            Log.d("CofrinhoActivity", "Navegando para EditMeta")
            intent = Intent(this, EditMeta::class.java)

            val metaId = ""
            val tituloMeta = ""
            val valorMeta = 0.0
            val qtnMeses = 0

            // Enviar os dados para a EditMeta Activity
            intent.putExtra("metaId", metaId)
            intent.putExtra("tituloMeta", tituloMeta)
            intent.putExtra("valorMeta", valorMeta)
            intent.putExtra("qtnMeses", qtnMeses)

            startActivity(intent)
        }

        // Buscar metas e atualizar as informações
        buscarMetas()
    }

    override fun onResume() {
        super.onResume()
        Log.d("CofrinhoActivity", "onResume chamado")
        // Recarregar as metas quando a atividade for retomada
        buscarMetas()
    }

    private fun buscarMetas() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        Log.d("CofrinhoActivity", "Buscando metas para o usuário: $userId")

        if (userId != null) {
            val metasRef = db.collection("Metas").whereEqualTo("userId", userId)

            metasRef.get()
                .addOnSuccessListener { documents ->
                    Log.d("CofrinhoActivity", "Metas recebidas: ${documents.size()}")

                    // Inicialize as listas de dados
                    val metas = mutableListOf<Triple<String, Double, Int>>()
                    val valorEconomizadoList = mutableListOf<Double>()
                    val valorPorMesList = mutableListOf<Double>()
                    val progressoList = mutableListOf<String>()
                    val progressValues = mutableListOf<Int>() // Novo: progresso em %

                    var totalValorMensal = 0.0

                    if (documents.isEmpty) {  // Verifique com .isEmpty em vez de isNotEmpty
                        Toast.makeText(this, "Nenhuma meta encontrada!", Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in documents) {
                            val metaId = document.id
                            val tituloMeta = document.getString("tituloMeta") ?: continue
                            val valorMeta = document.getDouble("valorMeta") ?: continue
                            val qtnMeses = document.getLong("qtnMeses")?.toInt() ?: 0
                            val mesesConcluidos = document.getLong("mesesConcluidos")?.toInt() ?: 0

                            metas.add(Triple(tituloMeta, valorMeta, qtnMeses))

                            if (qtnMeses > 0) {
                                val valorEconomizado = valorMeta / qtnMeses
                                valorEconomizadoList.add(valorEconomizado)
                            }

                            if (qtnMeses > 0) {
                                val valorPorMes = valorMeta / qtnMeses
                                valorPorMesList.add(valorPorMes)
                                totalValorMensal += valorPorMes
                            }

                            val progressoMeta = "$mesesConcluidos de $qtnMeses meses"
                            progressoList.add(progressoMeta)

                            // Novo: calculando progresso em %
                            if (qtnMeses > 0) {
                                val progresso = maxOf((mesesConcluidos.toDouble() / qtnMeses * 100).toInt(), 2)
                                progressValues.add(progresso)
                            }
                        }

                        // Atualizando adaptadores
                        metasAdapter.submitList(metas)
                        valorEconomizadoAdapter.submitList(valorEconomizadoList)
                        valorPorMesAdapter.submitList(valorPorMesList)
                        progressoAdapter = ProgressoAdapter(this, progressoList)
                        viewPagerProgresso.adapter = progressoAdapter

                        // Atualizando ProgressBarAdapter
                        progressBarAdapter = ProgressBarAdapter(this, progressValues)
                        viewPagerProgressBar.adapter = progressBarAdapter

                        binding.textView10.text = "R$: %.2f".format(totalValorMensal)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CofrinhoActivity", "Erro ao buscar as metas: ${e.message}", e)
                    Toast.makeText(this, "Erro ao buscar as metas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }
}


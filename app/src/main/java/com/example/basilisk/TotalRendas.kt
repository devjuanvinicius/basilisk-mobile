package com.example.basilisk

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basilisk.database.RendaDAO
import com.example.basilisk.databinding.ActivityTotalRendasBinding
import com.example.basilisk.recyclers.ItemAdapterRendas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TotalRendas : AppCompatActivity() {

    private lateinit var binding: ActivityTotalRendasBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTotalRendasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val idUsuario = auth.currentUser?.uid ?: ""
        val rendasDAO = RendaDAO(db, auth)

        val customFont = Typeface.createFromAsset(assets, "fonts/custom_font.ttf")

        // Ouvir as rendas em tempo real
        rendasDAO.ouvirRendas(
            idUsuario = idUsuario,
            onChange = { rendasList ->
                val recyclerView = binding.rvtotalrendas
                recyclerView.layoutManager = LinearLayoutManager(this)

                // Passando a função de exclusão para o adaptador
                recyclerView.adapter = ItemAdapterRendas(rendasList.toMutableList()) { idRenda ->
                    // Função de exclusão
                    rendasDAO.deletarRenda(
                        idUsuario = idUsuario,
                        idRenda = idRenda,
                        onSuccess = {
                            // Atualiza a RecyclerView após a exclusão
                            rendasDAO.retornarRenda(idUsuario, { rendas ->
                                (binding.rvtotalrendas.adapter as ItemAdapterRendas).updateList(rendas)
                            }, { exception ->
                                Log.e("TotalRendasActivity", "Erro ao atualizar lista de rendas: ${exception.message}")
                            })
                        },
                        onFailure = { exception ->
                            Log.e("TotalRendasActivity", "Erro ao deletar renda: ${exception.message}")
                        }
                    )
                }
            },
            onFailure = { exception ->
                Log.e("TotalRendasActivity", "Erro ao buscar rendas: ${exception.message}")
            }
        )

        // Função para retornar as rendas inicialmente
        rendasDAO.retornarRenda(
            idUsuario = idUsuario,
            onSuccess = { rendasList ->
                val recyclerView = binding.rvtotalrendas
                recyclerView.layoutManager = LinearLayoutManager(this)

                // Passando a função de exclusão para o adaptador
                recyclerView.adapter = ItemAdapterRendas(rendasList.toMutableList()) { idRenda ->
                    // Função de exclusão
                    rendasDAO.deletarRenda(
                        idUsuario = idUsuario,
                        idRenda = idRenda,
                        onSuccess = {
                            // Atualiza a RecyclerView após a exclusão
                            rendasDAO.retornarRenda(idUsuario, { rendas ->
                                (binding.rvtotalrendas.adapter as ItemAdapterRendas).updateList(rendas)
                            }, { exception ->
                                Log.e("TotalRendasActivity", "Erro ao atualizar lista de rendas: ${exception.message}")
                            })
                        },
                        onFailure = { exception ->
                            Log.e("TotalRendasActivity", "Erro ao deletar renda: ${exception.message}")
                        }
                    )
                }
            },
            onFailure = { exception ->
                Log.e("TotalRendasActivity", "Erro ao buscar rendas: ${exception.message}")
            }
        )
    }

    fun irParaDash(view: View) {
        val intent = Intent(view.context, MainActivity::class.java)
        view.context.startActivity(intent)
    }
}

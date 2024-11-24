package com.example.basilisk.database

import android.util.Log
import com.example.basilisk.model.Despesas
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson

class DespesasDAO(private val db: FirebaseFirestore): IDespesasDAO {

    override fun criarDespesa(
        idUsuario: String,
        despesa: Despesas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if(idUsuario.isNotEmpty()){
            db.collection("users").document(idUsuario)
                .set(
                    mapOf("despesa" to FieldValue.arrayUnion(despesa)),
                    SetOptions.merge()
                )
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("ID do usuário é nulo"))
        }
    }

    override fun deletarDespesa(
        idUsuario: String,
        idDespesas: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty() && idDespesas.isNotEmpty()) {
            retornarDespesa(idUsuario, { despesas ->
                val despesasAtualizadas = despesas.filter { it.id != idDespesas }

                db.collection("users").document(idUsuario)
                    .update("despesa", despesasAtualizadas)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }, { exception ->
                onFailure(exception)
            })
        } else {
            onFailure(Exception("ID do usuário é nulo"))
        }
    }

    override fun editarDespesa(
        idUsuario: String,
        despesa: Despesas,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty() && despesa != null) {
            retornarDespesa(idUsuario, { despesas ->
                val despesasAtualizadas = despesas.map { item ->
                    if (item.id == despesa.id) {
                        item.copy(
                            nome = despesa.nome,
                            valor = despesa.valor,
                            parcelas = despesa.parcelas,
                            despesaFixa = despesa.despesaFixa,
                            dataPagamento = despesa.dataPagamento,
                        )
                    } else {
                        item
                    }
                }

                db.collection("users").document(idUsuario)
                    .update(
                        "despesa", despesasAtualizadas
                    )
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)
                    }
            }, { exception ->
                onFailure(exception)
            })
        } else {
            onFailure(Exception("ID do usuário ou despesa inválidos"))
        }
    }

    override fun retornarDespesa(
        idUsuario: String,
        onSuccess: (List<Despesas>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            db.collection("users").document(idUsuario).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val despesasList = document.get("despesa") as? List<Map<String, Any>> ?: emptyList()

                        val despesas = despesasList.mapNotNull { despesaMap ->
                            try {
                                val json = Gson().toJson(despesaMap)
                                Gson().fromJson(json, Despesas::class.java)
                            } catch (e: Exception) {
                                Log.e("DespesasDAO", "Erro ao mapear despesa: ${e.message}")
                                null
                            }
                        }

                        onSuccess(despesas)
                    } else {
                        onFailure(Exception("Documento não encontrado no Firebase."))
                    }
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } else {
            onFailure(Exception("ID do usuário é inválido ou está vazio."))
        }
    }
}
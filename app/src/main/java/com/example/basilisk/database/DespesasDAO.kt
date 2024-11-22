package com.example.basilisk.database

import com.example.basilisk.model.Despesas
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DespesasDAO(private val db: FirebaseFirestore, private val auth: FirebaseAuth): IDespesasDAO {

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
        db.collection("users").document(idUsuario).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val despesas = document.get("despesa") as? List<Map<String, Any>> ?: emptyList()
                    val despesasList = despesas.map {
                        Despesas(
                            id = it["id"] as String,
                            nome = it["nome"] as String,
                            valor = it["valor"] as Double,
                            despesaFixa = it["despesaFixa"] as Boolean,
                            dataPagamento = it["dataPagamento"] as String,
                            tag = it["tag"] as String,
                            parcelas = it["parcelas"] as Int
                        )
                    }
                    onSuccess(despesasList)
                } else {
                    onFailure(Exception("Documento não encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
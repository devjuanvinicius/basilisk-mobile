package com.example.basilisk.database

import android.util.Log
import com.example.basilisk.model.Investimento
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson


class InvestimentoDAO(private val db: FirebaseFirestore): IInvestimentoDAO {
    override fun criarInvestimento(
        idUsuario: String,
        investimento: Investimento,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if(idUsuario.isNotEmpty()){
            db.collection("users").document(idUsuario)
                .set(
                    mapOf("investimentos" to FieldValue.arrayUnion(investimento)),
                    SetOptions.merge()
                )
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("ID do usuário é nulo"))
        }
    }

    override fun deletarInvestimento(
        idUsuario: String,
        codigoAcao: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty() && codigoAcao.isNotEmpty()) {
            retornarInvestimento(idUsuario, { investimento ->
                val investimentosAtualizadas = investimento.filter { it.codigoAcao != codigoAcao }

                db.collection("users").document(idUsuario)
                    .update("investimentos", investimentosAtualizadas)
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

    override fun editarInvestimento(
        idUsuario: String,
        investimento: Investimento,
        codigoAcao: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            retornarInvestimento(idUsuario, { investimentos ->
                val investimentoAtualizadas = investimentos.map { item ->
                    if (item.codigoAcao == codigoAcao) {
                        item.copy(
                            codigoAcao = investimento.codigoAcao,
                            dataCompra = investimento.dataCompra,
                            qtdAcoes = investimento.qtdAcoes,
                            valor = investimento.valor,
                            nomeAcao = investimento.nomeAcao
                        )
                    } else {
                        item
                    }
                }

                db.collection("users").document(idUsuario)
                    .update(
                        "investimentos", investimentoAtualizadas
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

    override fun retornarInvestimento(
        idUsuario: String,
        onSuccess: (List<Investimento>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(idUsuario).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val investimentoList = document.get("investimentos") as? List<Map<String, Any>> ?: emptyList()

                    val investimentos = investimentoList.mapNotNull { investimentosMap ->
                        try {
                            val json = Gson().toJson(investimentosMap)
                            Gson().fromJson(json, Investimento::class.java)
                        } catch (e: Exception) {
                            Log.e("InvestimentoDAO", "Erro ao mapear despesa: ${e.message}")
                            null
                        }
                    }

                    onSuccess(investimentos)
                } else {
                    onFailure(Exception("Documento não encontrado no Firebase."))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
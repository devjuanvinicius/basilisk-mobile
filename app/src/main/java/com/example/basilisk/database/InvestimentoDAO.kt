package com.example.basilisk.database

import com.example.basilisk.model.Investimento
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            retornarInvestimento(idUsuario, { investimentos ->
                val investimentoAtualizadas = investimentos.map { item ->
                    if (item.codigoAcao == investimento.codigoAcao) {
                        item.copy(
                            codigoAcao = investimento.codigoAcao,
                            dataCompra = investimento.dataCompra,
                            qtdAcoes = investimento.qtdAcoes,
                            valor = investimento.valor,
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
                    val investimentos = document.get("investimentos") as? List<Map<String, Any>> ?: emptyList()
                    val investimentosList = investimentos.map {
                        Investimento(
                            codigoAcao = it["codigoAcao"] as String,
                            dataCompra = it["dataCompra"] as String,
                            qtdAcoes = it["qtdAcoes"] as Int,
                            valor = it["valor"] as Double
                        )
                    }
                    onSuccess(investimentosList)
                } else {
                    onFailure(Exception("Documento não encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
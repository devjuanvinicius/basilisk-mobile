package com.example.basilisk.database

import com.example.basilisk.model.Renda
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class RendaDAO(private val db: FirebaseFirestore): IRendaDAO {
    override fun criarRenda(
        idUsuario: String,
        renda: Renda,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if(idUsuario.isNotEmpty()){
            db.collection("users").document(idUsuario)
                .set(
                    mapOf("rendas" to FieldValue.arrayUnion(renda)),
                    SetOptions.merge()
                )
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("ID do usuário é nulo"))
        }
    }

    override fun deletarRenda(
        idUsuario: String,
        idRenda: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty() && idRenda.isNotEmpty()) {
            retornarRenda(idUsuario, { rendas ->
                val rendasAtualizadas = rendas.filter { it.id != idRenda }

                db.collection("users").document(idUsuario)
                    .update("rendas", rendasAtualizadas)
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

    override fun editarRenda(
        idUsuario: String,
        renda: Renda,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            retornarRenda(idUsuario, { rendas ->
                val rendasAtualizadas = rendas.map { item ->
                    if (item.id == renda.id) {
                        item.copy(
                            id = renda.id,
                            nome = renda.nome,
                            valor = renda.valor,
                            dataRecebimento = renda.dataRecebimento,
                            rendaFixa = renda.rendaFixa
                        )
                    } else {
                        item
                    }
                }

                db.collection("users").document(idUsuario)
                    .update(
                        "rendas", rendasAtualizadas
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

    override fun retornarRenda(
        idUsuario: String,
        onSuccess: (List<Renda>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("users").document(idUsuario).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rendas = document.get("renda") as? List<Map<String, Any>> ?: emptyList()
                    val rendasList = rendas.map {
                        Renda(
                            id = it["id"] as String,
                            nome = it["nome"] as String,
                            valor = it["valor"] as Double,
                            dataRecebimento = it["dataPagamento"] as String,
                            rendaFixa = it["rendaFixa"] as Boolean
                        )
                    }
                    onSuccess(rendasList)
                } else {
                    onFailure(Exception("Documento não encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
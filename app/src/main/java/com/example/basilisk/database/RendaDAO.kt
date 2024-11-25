package com.example.basilisk.database

import com.example.basilisk.model.Renda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson

class RendaDAO(private val db: FirebaseFirestore, private val auth: FirebaseAuth) : IRendaDAO {

    override fun criarRenda(
        idUsuario: String,
        renda: Renda,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
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
            onFailure(Exception("ID do usuário ou ID da renda são nulos"))
        }
    }

    override fun editarRenda(
        idUsuario: String,
        renda: Renda,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty() && renda != null) {
            retornarRenda(idUsuario, { rendas ->
                val rendasAtualizadas = rendas.map { item ->
                    if (item.id == renda.id) {
                        item.copy(
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
            onFailure(Exception("ID do usuário ou renda inválidos"))
        }
    }

    override fun retornarRenda(
        idUsuario: String,
        onSuccess: (List<Renda>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            db.collection("users").document(idUsuario).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val rendasList = document.get("rendas") as? List<Map<String, Any>> ?: emptyList()

                        val rendas = rendasList.mapNotNull { rendaMap ->
                            try {
                                val json = Gson().toJson(rendaMap)
                                Gson().fromJson(json, Renda::class.java)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        onSuccess(rendas)
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

    fun ouvirRendas(
        idUsuario: String,
        onChange: (List<Renda>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            db.collection("users").document(idUsuario)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        onFailure(exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val rendasList = snapshot.get("rendas") as? List<Map<String, Any>> ?: emptyList()

                        val rendas = rendasList.mapNotNull { rendaMap ->
                            try {
                                val json = Gson().toJson(rendaMap)
                                Gson().fromJson(json, Renda::class.java)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        onChange(rendas)
                    } else {
                        onChange(emptyList()) // Documento ainda não existe ou está vazio
                    }
                }
        } else {
            onFailure(Exception("ID do usuário é inválido ou está vazio."))
        }
    }
}

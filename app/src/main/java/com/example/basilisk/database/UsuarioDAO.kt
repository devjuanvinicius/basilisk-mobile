package com.example.basilisk.database

import com.example.basilisk.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioDAO(private val db: FirebaseFirestore, private val auth: FirebaseAuth): IUsuarioDAO {

    override fun criarUsuario(
        usuario: Usuario,
        senha: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(usuario.email, senha)
            .addOnCompleteListener { cadastro ->
                if (cadastro.isSuccessful) {
                    val userId = cadastro.result?.user?.uid
                    if (userId != null) {
                        usuario.id = userId
                        salvarUsuarioFirestore(usuario, onSuccess, onFailure)
                    } else {
                        onFailure(Exception("Erro ao obter UID do usuário"))
                    }
                } else {
                    onFailure(cadastro.exception ?: Exception("Erro desconhecido ao criar usuário"))
                }
            }
    }

    override fun salvarUsuarioFirestore(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuario.id?.let { id ->
            db.collection("users").document(id)
                .set(usuario)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { exception -> onFailure(exception) }
        } ?: onFailure(Exception("ID do usuário é nulo"))
    }

    override fun editarUsuario(
        usuario: Usuario,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (usuario.id != null) {
            val referenciaUsuario = db
                .collection("users")
                .document(usuario.id!!)

            referenciaUsuario.update(
                mapOf(
                    "nome" to usuario.nome,
                    "email" to usuario.email,
                    "telefone" to usuario.telefone,
                    "dataNascimento" to usuario.dataNascimento
                )
            )
                .addOnCompleteListener{onSuccess()}
                .addOnFailureListener { exception -> onFailure(exception) }
        }
    }

    override fun deletarUsuario(
        idUsuario: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (idUsuario.isNotEmpty()) {
            val referenciaUsuario = db
                .collection("users")
                .document(idUsuario)
                .delete()
                .addOnCompleteListener{onSuccess()}
                .addOnFailureListener { exception -> onFailure(exception) }
        } else {
            onFailure(Exception("ID de usuário inválido"))
        }
    }

    override fun retornarUsuario(
        idUsuario: String,
        onSuccess: (Usuario?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        if(idUsuario.isNotEmpty()){
            val referenciaUsuario = db
                .collection("users")
                .document(idUsuario)

            referenciaUsuario
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dados = documentSnapshot.data
                    if (dados != null){
                        val usuario = Usuario(
                            nome = dados["nome"] as? String ?: "Nome não disponível",
                            id = dados["id"] as? String ?: "ID não disponível",
                            email = dados["email"] as? String ?: "Email não disponível",
                            dataNascimento = dados["dataNascimento"] as? String ?: "Data de nascimento não disponível",
                            telefone = dados["telefone"] as? String ?: "Telefone não disponível"
                        )

                        onSuccess(usuario)
                    } else {
                        onFailure(Exception("Usuário não encontrado"))
                    }
                }
                .addOnFailureListener{exception -> onFailure(exception)}
        } else {
            onFailure(Exception("ID de usuário inválido"))
        }
    }
}
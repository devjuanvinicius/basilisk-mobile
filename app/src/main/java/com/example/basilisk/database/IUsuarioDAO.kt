package com.example.basilisk.database

import com.example.basilisk.model.Usuario

interface IUsuarioDAO {
    fun criarUsuario(usuario: Usuario, senha: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun salvarUsuarioFirestore(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun editarUsuario(usuario: Usuario, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deletarUsuario(idUsuario: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun retornarUsuario(idUsuario: String, onSuccess: (Usuario?) -> Unit, onFailure: (Exception) -> Unit)
}

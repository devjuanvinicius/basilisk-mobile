package com.example.basilisk.database

import com.example.basilisk.model.Renda

interface IRendaDAO {
    fun criarRenda(idUsuario: String, renda: Renda, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deletarRenda(idUsuario: String, idRenda: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun editarRenda(idUsuario: String, renda: Renda, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun retornarRenda(idUsuario: String, onSuccess: (List<Renda>) -> Unit, onFailure: (Exception) -> Unit)
}
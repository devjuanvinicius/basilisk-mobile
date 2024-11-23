package com.example.basilisk.database

import com.example.basilisk.model.Despesas

interface IDespesasDAO {
    fun criarDespesa(idUsuario: String, despesa: Despesas, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deletarDespesa(idUsuario: String, idDespesas: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun editarDespesa(idUsuario: String, despesa: Despesas, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun retornarDespesa(idUsuario: String, onSuccess: (List<Despesas>) -> Unit, onFailure: (Exception) -> Unit)
}
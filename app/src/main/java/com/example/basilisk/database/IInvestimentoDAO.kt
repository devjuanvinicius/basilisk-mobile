package com.example.basilisk.database

import com.example.basilisk.model.Investimento

interface IInvestimentoDAO {
    fun criarInvestimento(idUsuario: String, investimento: Investimento, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun deletarInvestimento(idUsuario: String, codigoAcao: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun editarInvestimento(idUsuario: String, investimento: Investimento, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun retornarInvestimento(idUsuario: String, onSuccess: (List<Investimento>) -> Unit, onFailure: (Exception) -> Unit)
}
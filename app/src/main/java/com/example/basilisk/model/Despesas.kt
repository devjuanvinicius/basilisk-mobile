package com.example.basilisk.model

data class Despesas(
    val id: String,
    val nome: String,
    val parcelas: Int,
    val dataPagamento: String,
    val despesaFixa: Boolean,
    val valor: Double
)

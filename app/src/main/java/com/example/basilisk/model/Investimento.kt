package com.example.basilisk.model

data class Investimento(
    val codigoAcao: String,
    val dataCompra: String,
    val qtdAcoes: Int,
    val valor: Double
)

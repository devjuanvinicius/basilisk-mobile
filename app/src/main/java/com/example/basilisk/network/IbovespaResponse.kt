package com.example.basilisk.network

data class IbovespaResponse(
    val results: List<IbovespaResult>?
)

data class IbovespaResult(
    val regularMarketPrice: Double,
    val regularMarketChangePercent: Double
)

package com.example.basilisk.network

data class ApiResponse(
    val results: List<ApiResult>?
)

data class ApiResult(
    val currency: String,
    val shortName: String,
    val longName: String,
    val regularMarketChange: Double,
    val regularMarketChangePercent: Double,
    val regularMarketTime: String,
    val regularMarketPrice: Double,
    val regularMarketDayHigh: Double,
    val regularMarketDayRange: String,
    val regularMarketDayLow: Double,
    val regularMarketVolume: Long,
    val regularMarketPreviousClose: Double,
    val regularMarketOpen: Double,
    val fiftyTwoWeekRange: String,
    val fiftyTwoWeekLow: Double,
    val fiftyTwoWeekHigh: Double,
    val symbol: String,
    val priceEarnings: Double,
    val earningsPerShare: Double,
    val logourl: String
)

// RetrofitClient.kt
package com.example.basilisk.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://brapi.dev/"  // URL base da API

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())  // GSON para conversão de JSON
        .build()

    // Aqui você cria a instância do serviço BrapiService
    val api: BrapiService = retrofit.create(BrapiService::class.java)
}

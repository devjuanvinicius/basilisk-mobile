package com.example.basilisk.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BrapiService {

    @GET("api/quote/{acao}")
    fun getAcao(
        @Path("acao") acao: String,  // Aqui você passa o valor da ação
        @Query("token") token: String
    ): Call<ApiResponse>
}


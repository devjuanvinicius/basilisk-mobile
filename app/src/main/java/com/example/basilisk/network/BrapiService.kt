package com.example.basilisk.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BrapiService {


    @GET("api/quote/^BVSP")
    fun getIbovespa(
        @Query("token") token: String
    ): Call<IbovespaResponse>
}

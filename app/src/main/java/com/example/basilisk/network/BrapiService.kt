package com.example.basilisk.network

import retrofit2.Call
import retrofit2.http.GET

interface BrapiService {

    @GET("api/quote/^BVSP?token=6AfdujsFQpyPMnwfgeNWFf")
    fun getIbovespa(): Call<IbovespaResponse>
}

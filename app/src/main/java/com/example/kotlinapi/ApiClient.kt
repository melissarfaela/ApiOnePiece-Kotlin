package com.example.kotlinapi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.api-onepiece.com/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

interface ApiService {
    @GET("fruits/en")
    suspend fun getFruits(): List<Fruit>
}
package com.example.bonusproject.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/ds/query")
    fun getTemperatureData(@Header("Authorization") token: String, @Body requestBody: RequestBody): Call<ApiResponse>
}

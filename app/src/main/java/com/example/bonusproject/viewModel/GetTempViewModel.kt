package com.example.bonusproject.viewModel

import androidx.lifecycle.ViewModel
import com.example.bonusproject.api.ApiClient
import com.example.bonusproject.api.ApiResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetTempViewModel : ViewModel() {
    fun getData(
        onSuccess: (ApiResponse) -> Unit,
    ) {
        val apiInterface = ApiClient.api
        val token = "glsa_2JTFbJruTx7YMCdcIXBcVQqfT1Am8VB3_5f8b1c2e"
        val authHeader = "Bearer $token"


        val requestBody = """
{
    "queries": [{
        "datasource": {
            "type": "influxdb",
            "uid": "ebfaf006-f8aa-460f-a47a-bf3530dc9bf4"
        },
        "query": "from(bucket:\"sensors\")\n    |> range(start: -3h)\n    |> filter(fn: (r) => r[\"_measurement\"] == \"temperature\")\n    |> aggregateWindow(every: 5m, fn: mean, createEmpty: false)"
    }],
    "from": "now-3h"
}
""".trimIndent().toRequestBody("application/json".toMediaType())


        val call = apiInterface.getTemperatureData(authHeader, requestBody)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onSuccess.invoke(responseBody)
                    }

                } else {
                    println("Request failed with status code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                println("Request failed: ${t.message}")                }
        })
    }
}
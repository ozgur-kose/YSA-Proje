package com.example.plantalarm

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


data class PlantAiRequest(
    val degerler: List<Float>
)


data class PlantAiResponse(
    val durum: String? = null,
    val risk_seviyesi: String? = null,
    val model_guven_skoru: String? = null,
    val hata: String? = null
)

interface PlantAiApiService {
    @POST("tahmin-yap")
    suspend fun predictState(@Body request: PlantAiRequest): PlantAiResponse

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8000/"

        fun create(): PlantAiApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PlantAiApiService::class.java)
        }
    }
}
package com.aquaspoof.unified.toolkit.mcpe

import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @GET("scripts")
    suspend fun getScripts(): List<ScriptItem>

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): ResponseBody
    @GET("configs")
    suspend fun getConfigs(): List<ConfigItem>

    companion object {
        private const val BASE_URL = "https://ut-mcpe.org"

        val instance: ApiService by lazy {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
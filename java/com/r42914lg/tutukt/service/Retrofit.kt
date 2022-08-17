package com.r42914lg.tutukt.service

import com.google.gson.Gson
import com.r42914lg.tutukt.domain.Category
import com.r42914lg.tutukt.domain.CategoryDetailed
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface APIJService {
    @GET("categories")
    suspend fun getCategories(
        @Query("count") amountOfCluesToReturn: Int,
        @Query("offset") offset: Int
    ): Response<List<Category>>

    @GET("category")
    suspend fun getDetailedCategory(@Query("id") categoryId: Int): Response<CategoryDetailed>
}

class RestClient private constructor() {
    private val api: APIJService

    companion object {
        private const val API_URL = "https://jservice.io/api/"
        private val instance = RestClient()

        fun gson(): Gson {
            return Gson()
        }

        private fun logLevel(): OkHttpClient {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        fun getApi(): APIJService = instance.api
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(logLevel())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(APIJService::class.java)
    }
}
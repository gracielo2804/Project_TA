package com.gracielo.projectta.data.source.remote.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    fun provideApiService(): ApiEndPoint {
        val retrofit = Retrofit.Builder()
            .baseUrl(  //tipe method pada web service
                "https://skripsicielo.masuk.id/")
//                "http://192.168.0.109/P10-myservice_php/myservice/master.php/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
        return retrofit.create(ApiEndPoint::class.java)
    }
}
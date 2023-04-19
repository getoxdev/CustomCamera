package com.customcamera.data.remote

import android.util.Log
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Logger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://apistaging.inito.com/"

    private val okHttpClient = OkHttpClient()
        .newBuilder()
        .addInterceptor(NetworkInterceptor)
        .addInterceptor(CurlInterceptor(object : Logger {
            override fun log(message: String) {
                Log.v("OkHttpCurl", message)
            }
        }))
        .build()

    fun getClient(): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

object NetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeader = chain.request()
            .newBuilder()
            .header(
                "Content-Type", "application/json"
            ).build()
        return chain.proceed(requestWithHeader)
    }
}
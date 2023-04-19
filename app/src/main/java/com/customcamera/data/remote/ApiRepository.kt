package com.customcamera.data.remote

import com.customcamera.data.remote.m.SignInResponse
import okhttp3.MultipartBody
import retrofit2.Response

class ApiRepository {

    private val apiService = RetrofitClient.getClient().create(ApiService::class.java)

    suspend fun executeSignIn(): Response<SignInResponse> {
        return apiService.executeSignIn()
    }

    suspend fun sendTestImage(headers: Map<String,String>,testData: MultipartBody.Part): Response<Any> {
        return apiService.sendTestImage(headers,testData)
    }
}
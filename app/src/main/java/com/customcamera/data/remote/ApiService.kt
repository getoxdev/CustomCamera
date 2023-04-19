package com.customcamera.data.remote

import com.customcamera.data.remote.m.SignInResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("auth/sign_in")
    suspend fun executeSignIn()
            : Response<SignInResponse>

    @POST("tests")
    @Multipart
    suspend fun sendTestImage(
        @HeaderMap headers: Map<String, String>,
        @Part testData: MultipartBody.Part
    ): Response<Any>
}
package com.customcamera.data.remote.m

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SignInRequest(
    @SerializedName("email")
    val email: String = "amit_4@test.com",
    @SerializedName("password")
    val password: String = "12345678"
)

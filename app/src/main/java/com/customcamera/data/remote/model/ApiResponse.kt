package com.customcamera.data.remote.m

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SignInResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("onboarded")
    val onboarded: Boolean?,
)
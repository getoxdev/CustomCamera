package com.customcamera.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.customcamera.data.remote.ApiRepository
import com.customcamera.data.remote.m.SignInResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class CustomCameraViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = ApiRepository()

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _headers = MutableLiveData<Map<String,String>>()
    val headers: LiveData<Map<String,String>> = _headers

    private val _signInResp = MutableLiveData<SignInResponse?>()
    val signInResp: LiveData<SignInResponse?> = _signInResp

    private val _error = MutableLiveData<String?>()
    val error = _error

    fun executeSignIn() {
        viewModelScope.launch {
            try {
                val response = repository.executeSignIn()

                if(response.isSuccessful) {
                    _headers.value = response.headers().toMap()
                    _signInResp.value = response.body()
                } else {
                    _error.value = response.errorBody().toString()
                }
                Log.e("CustomCameraViewModel", response.toString())
                return@launch
            } catch (ex: Exception) {
                Log.e("CustomCameraViewModel", ex.toString())
            }
        }
    }

    fun sendTestImage(headers: Map<String,String>,testData: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val response = repository.sendTestImage(headers,testData)

                if(!response.isSuccessful) {
                    _success.value = false
                    _error.value = response.errorBody().toString()
                } else {
                    _success.value = true
                }

                Log.e("CustomCameraViewModel", response.toString())
                return@launch
            } catch (ex: Exception) {
                Log.e("CustomCameraViewModel", ex.toString())
            }
        }
    }
}
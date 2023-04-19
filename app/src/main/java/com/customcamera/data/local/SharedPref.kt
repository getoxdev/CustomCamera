package com.customcamera.data.local

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    private lateinit var sharedPreferences: SharedPreferences

    fun initSharedPreferences(context: Context): SharedPreferences {
        sharedPreferences =
            context.getSharedPreferences("shared_preferences_custom_camera", Context.MODE_PRIVATE)

        return sharedPreferences
    }

    fun getPrefString(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun setPrefString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
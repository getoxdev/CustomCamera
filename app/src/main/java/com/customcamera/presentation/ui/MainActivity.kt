package com.customcamera.presentation.ui

import android.Manifest
import android.app.ActivityManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.customcamera.databinding.ActivityMainBinding
import com.customcamera.presentation.ui.fragments.Screen1Fragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        if (allPermissionsGranted()) {
            showScreen1()
        } else {
            ActivityCompat.requestPermissions(
                this,
                getRequiredPermissions(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun showScreen1() {
        supportFragmentManager
            .beginTransaction()
            .add(binding.fcvScreen.id, Screen1Fragment())
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                showScreen1()
            } else {
                Toast.makeText(
                    this, "Please grant the required permissions",
                    Toast.LENGTH_SHORT
                ).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    (this.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                }, 2000)
            }
        }
    }

    private fun allPermissionsGranted() = getRequiredPermissions().all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val TAG = "HomeActivity"
        const val PERMISSION_REQUEST_CODE = 101
        private fun getRequiredPermissions(): Array<String> {
            return arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}
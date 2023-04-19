package com.customcamera.presentation.ui.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.Parameters.FOCUS_MODE_FIXED
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.customcamera.R
import com.customcamera.databinding.FragmentScreen2Binding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class Screen2Fragment : Fragment() {
    private var _binding: FragmentScreen2Binding? = null
    private val binding get() = _binding!!

    private var mCamera: Camera? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreen2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseCamera()
        countDownTimer?.cancel()
        _binding = null
    }

    private fun initUI() {
        binding.cpiTimer.progress = 100

        if (!checkCameraHardware(requireContext())) {
            Toast.makeText(requireContext(), "No Camera Found", Toast.LENGTH_LONG).show()
        } else {
            mCamera = getCameraInstance()

            setCameraConfig(mCamera?.parameters)

            try {
                mCamera?.setPreviewTexture(SurfaceTexture(10))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mCamera?.startPreview()
            mCamera?.takePicture(null, null, mPicture)
        }
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun getCameraInstance(): Camera? {
        return try {
            Camera.open()
        } catch (e: Exception) {
            null
        }
    }

    private fun setCameraConfig(mCameraParams: Camera.Parameters?) {
        mCameraParams?.setRotation(90)
        mCameraParams?.set("focus-distances", "1,1,1")
        mCameraParams?.focusMode = FOCUS_MODE_FIXED

        mCamera?.parameters = mCameraParams
    }

    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputImageFile() ?: run {
            Log.d(TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: ${e.message}")
        } finally {
            mCamera?.stopPreview()
            releaseCamera()
        }

        showCircularTimer()
    }

    private fun getOutputImageFile(): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "CustomCameraApp"
        )

        mediaStorageDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory")
                    return null
                }
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        return File("${mediaStorageDir.path}${File.separator}IMG_$timeStamp.jpg")
    }

    private fun showCircularTimer() {
        binding.apply {
            tvCapture.text = "Captured Single Image"

            var countDownSeconds: Long = 300

            countDownTimer = object : CountDownTimer(300 * 1000, 5000) {
                override fun onTick(millisUntilFinished: Long) {
                    countDownSeconds -= 5
                    tvTimer.text = countDownSeconds.toInt().toString()

                    val updateTimerProgress: Int =
                        ((countDownSeconds / (300 * 1f)) * 100).toInt()

                    cpiTimer.progress = updateTimerProgress
                }

                override fun onFinish() {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcv_screen, Screen3Fragment())
                        .addToBackStack(null)
                        .commit()
                }
            }.start()
        }
    }

    private fun releaseCamera() {
        mCamera?.release()
        mCamera = null
    }

    companion object {
        const val TAG = "Screen2Fragment"
    }
}
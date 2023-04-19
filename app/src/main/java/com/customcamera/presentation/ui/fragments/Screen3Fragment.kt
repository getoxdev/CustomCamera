package com.customcamera.presentation.ui.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.customcamera.databinding.FragmentScreen3Binding
import com.customcamera.presentation.CustomCameraViewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class Screen3Fragment : Fragment() {
    private var _binding: FragmentScreen3Binding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomCameraViewModel

    private var mCamera: Camera? = null
    private var headers = mutableMapOf<String,String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreen3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CustomCameraViewModel::class.java]

        initUI()
        observeChanges()
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releaseCamera()
        _binding = null
    }

    private fun initUI() {
        if (!checkCameraHardware(requireContext())) {
            Toast.makeText(requireContext(), "No Camera Found", Toast.LENGTH_LONG).show()
        } else {
            binding.apply {
                mCamera = getCameraInstance()

                setCameraConfig(mCamera?.parameters)

                val mCameraParams = mCamera?.parameters
                mCamera?.parameters?.exposureCompensation =
                    (12 / mCameraParams?.exposureCompensationStep!!).toInt()

                try {
                    mCamera?.setPreviewTexture(SurfaceTexture(10))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                tvCapture.text = "Capturing Image for Exposure: ${12} EV"
                mCamera?.startPreview()
                mCamera?.takePicture(null, null, mPicture)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun observeChanges() {
        viewModel.headers.observe(viewLifecycleOwner) {
            headers["access-token"] = it["access-token"].toString()
            headers["uid"] = it["uid"].toString()
            headers["client"] = it["client"].toString()
        }

        viewModel.signInResp.observe(viewLifecycleOwner) {

        }

        viewModel.success.observe(viewLifecycleOwner) {
            if(it) {
                binding.tvCapture.text = "Test Done"
            } else {
                binding.tvCapture.text = "Test Failed"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
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
        mCameraParams?.focusMode = Camera.Parameters.FOCUS_MODE_FIXED

        mCamera?.parameters = mCameraParams
    }

    private val mPicture = Camera.PictureCallback { data, _ ->
        val pictureFile: File = getOutputImageFile() ?: run {
            Log.d(Screen2Fragment.TAG, ("Error creating media file, check storage permissions"))
            return@PictureCallback
        }

        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d(Screen2Fragment.TAG, "File not found: ${e.message}")
        } catch (e: IOException) {
            Log.d(Screen2Fragment.TAG, "Error accessing file: ${e.message}")
        } finally {
            mCamera?.stopPreview()
            releaseCamera()
        }

        //API Call with Desired Image out of the 24 clicked
        binding.tvCapture.text = "Sending Image"
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

    private fun releaseCamera() {
        mCamera?.release()
        mCamera = null
    }

    companion object {
        const val TAG = "Screen3Fragment"
    }
}
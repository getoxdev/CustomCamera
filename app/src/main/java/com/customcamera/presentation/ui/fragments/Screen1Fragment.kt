package com.customcamera.presentation.ui.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.customcamera.R
import com.customcamera.data.local.SharedPref
import com.customcamera.databinding.FragmentScreen1Binding


class Screen1Fragment : Fragment() {
    private var _binding: FragmentScreen1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreen1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SharedPref.initSharedPreferences(requireContext())

        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUI() {
        val sharedPrefName = SharedPref.getPrefString(KEY_NAME)
        val sharedPrefEmail = SharedPref.getPrefString(KEY_EMAIL)

        binding.apply {
            if (!sharedPrefName.isNullOrEmpty())
                tietName.setText(sharedPrefName)

            if (!sharedPrefEmail.isNullOrEmpty())
                tietEmail.setText(sharedPrefEmail)

            btnTakeTest.setOnClickListener {
                if (tietName.text.isNullOrEmpty() || tietEmail.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please enter the fields", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    SharedPref.setPrefString(KEY_NAME, tietName.text.toString())
                    SharedPref.setPrefString(KEY_EMAIL, tietEmail.text.toString())

                    hideKeyboard()

                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcv_screen, Screen2Fragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        const val TAG = "Screen1Fragment"
        const val KEY_NAME = "key_name"
        const val KEY_EMAIL = "key_email"
    }
}
package com.example.splash.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.splash.extensions.isValidEmail
import com.example.splash.R
import com.example.splash.commons.ProgressDialog
import com.example.splash.databinding.FragmentRegisterBinding
import com.example.splash.viewmodel.FirebaseAuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val authViewModel: FirebaseAuthViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        initView()

        return view
    }

    private fun initView() {
        showProgressBar()
        binding.apply {
            btnRegister.setOnClickListener {
                if (isValidData()) {
                    authViewModel.createUser(
                        etName.text.toString(),
                        etEmail.text.toString(),
                        etPhone.text.toString(),
                        etPassword.text.toString()
                    )
                }
            }
        }

        authViewModel.userLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Toast.makeText(requireContext(), "User ${it.email}  Logged in succesfuuly", Toast.LENGTH_SHORT)
                    .show()
                navigateToMainActivity()
            }
            authViewModel.progress.value = false
        })

    }

    private fun navigateToMainActivity() {
        findNavController().navigate(R.id.action_registerFragment_to_imageFragment)
    }

    private fun isValidData(): Boolean {
        binding.apply {

            if (etName.text.isNullOrEmpty() || etName.text.toString().length <= 3) {
                etName.error = "Please enter the valid name4"
                return false
            } else if (etEmail.text.isNullOrEmpty() || !etEmail.text.toString().isValidEmail()) {
                etEmail.error = "Please enter the valid email"
                return false
            } else if (etPhone.text.isNullOrEmpty() || etPhone.text.toString().length != 10) {
                etPhone.error = "Please enter the valid phone number"
                return false
            } else if (etPassword.text.isNullOrEmpty() || etPassword.text!!.length < 6) {
                etPassword.error = "Password Length should be greater than 6"
                return false
            } else if (etConfirmPassword.text.isNullOrEmpty() || (etPassword.text.toString() != etConfirmPassword.text.toString())) {
                etConfirmPassword.error = "Passwords Should Match"
                return false
            }
            return true
        }

    }

    private fun showProgressBar() {
        val dialog =  ProgressDialog.dialog(requireContext(), "signing in...")
        authViewModel.progress.observe(viewLifecycleOwner, Observer { showing ->
            if (showing) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        })
    }


}
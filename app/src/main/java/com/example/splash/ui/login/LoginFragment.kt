package com.example.splash.ui.login

import android.content.Intent
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
import com.example.splash.databinding.FragmentLoginBinding
import com.example.splash.utilites.Resource
import com.example.splash.viewmodel.FirebaseAuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val authViewModel: FirebaseAuthViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        initViews()

        return view

    }

    private fun initViews() {

        binding.buttonRegisterHere.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }


            showProgressBar()

            binding.apply {
                buttonLogin1.setOnClickListener {
                    if (isValidData()) {
                        authViewModel.loginUser(etEmail.text.toString(), etpassword.text.toString())
                    }
                }
            }

            authViewModel.loginState.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    is Resource.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "User  Logged in succesfuuly",
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToMainActivity()
                    }
                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "User  Errr",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is Resource.Loading -> {
                        Toast.makeText(
                            requireContext(),
                            "User  Loading",
                            Toast.LENGTH_LONG
                        ).show()
                    }


                }



            })

    }

    private fun isValidData(): Boolean {
        binding.apply {
            if (etEmail.text.isNullOrEmpty() || !etEmail.text.toString().isValidEmail()) {
                etEmail.error = "Please enter the valid email"
                return false
            } else if (etpassword.text.isNullOrEmpty() || etpassword.text!!.length < 6) {
                etpassword.error = "Password Length should be greater than 6"
                return false
            }
            return true
        }
    }
    private fun showProgressBar() {

        val dialog =  ProgressDialog.dialog(requireContext(), "Logging in...")
        authViewModel.progress.observe(requireActivity(), Observer { showing ->
            if (showing) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        })
    }
    private fun navigateToMainActivity() {
        findNavController().navigate(R.id.action_loginFragment_to_imageFragment)
    }


}
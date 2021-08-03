package com.example.splash.ui.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.splash.R
import com.example.splash.databinding.FragmentCityBinding
import com.example.splash.databinding.FragmentImageBinding
import com.example.splash.databinding.FragmentRegisterBinding


class CityFragment : Fragment() {
    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentCityBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.cityBtn.setOnClickListener {
            findNavController().navigate(R.id.action_cityFragment_to_splashActivity)

        }
        return view
    }



}

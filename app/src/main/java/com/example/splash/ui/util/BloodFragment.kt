package com.example.splash.ui.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.splash.R
import com.example.splash.databinding.FragmentBloodBinding
import com.example.splash.databinding.FragmentImageBinding


class BloodFragment : Fragment() {
    private var _binding: FragmentBloodBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentBloodBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.bGroupBtn.setOnClickListener {
            findNavController().navigate(R.id.action_bloodFragment_to_cityFragment)
        }
        return view
    }
}
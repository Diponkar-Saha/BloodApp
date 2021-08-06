package com.example.splash

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.splash.databinding.FragmentBlankBinding
import com.example.splash.model.Address
import com.example.splash.viewmodel.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlankFragment : Fragment() {
    private val viewModel: ProfileImageViewModel by viewModels()
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.textViewAddressNP.setOnClickListener {


            findNavController().navigate(R.id.action_blankFragment_to_pickMapsFragment)
        }





        return view

    }



}
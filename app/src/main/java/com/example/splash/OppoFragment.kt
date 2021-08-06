package com.example.splash

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.splash.databinding.FragmentBlankBinding
import com.example.splash.databinding.FragmentOppoBinding
import com.example.splash.model.Address


class OppoFragment : Fragment() {
    private var _binding: FragmentOppoBinding? = null
    private val binding get() = _binding!!
    private lateinit var address: Address


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOppoBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().let {


            address = it.getParcelable("latLngAddress")!!
            Log.i("TESTE", "$address")
            val addressString = "${address.street} - ${address.city}"
            binding.textViewAddressNP.text = addressString

        }

    }


}
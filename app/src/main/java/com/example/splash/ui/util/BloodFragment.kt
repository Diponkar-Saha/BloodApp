package com.example.splash.ui.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.splash.R
import com.example.splash.databinding.FragmentBloodBinding
import com.example.splash.databinding.FragmentImageBinding
import com.example.splash.utilites.Resource
import com.example.splash.viewmodel.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BloodFragment : Fragment() , View.OnClickListener{
    private val viewModel: ProfileImageViewModel by viewModels()
    private var _binding: FragmentBloodBinding? = null
    private val binding get() = _binding!!
    private var bloodGroup = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentBloodBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.aplus.setOnClickListener (this)
        binding.aminus.setOnClickListener (this)
        binding.bplus.setOnClickListener (this)
        binding.bminus.setOnClickListener (this)
        binding.oplus.setOnClickListener (this)
        binding.ominus.setOnClickListener (this)
        binding.abplus.setOnClickListener (this)
        binding.abminus.setOnClickListener (this)





        initView()


        return view

    }

    private fun initView() {

        binding.bGroupBtn.setOnClickListener {
            if(getValuesOfInputs()) {
                viewModel.bloodGroupUpload(bloodGroup)
            }else{
                Toast.makeText(requireContext(),"please select your blood group",Toast.LENGTH_SHORT).show()
            }
        }


        //
        viewModel.uploadStatus?.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is Resource.Loading -> {

                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Success -> {

                    Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Error -> {

                    Toast.makeText(requireContext(), "updated failed ${status.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        })

    }

    private fun getValuesOfInputs(): Boolean {
        if (bloodGroup == "") {
            Toast.makeText(requireContext(),"please select your blood group",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.aplus ->{
                bloodGroup="A+"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.aminus ->{
                bloodGroup="A-"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.bplus ->{
                bloodGroup="B+"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.bminus ->{
                bloodGroup="B-"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.oplus ->{
                bloodGroup="O+"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.ominus ->{
                bloodGroup="O-"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.abplus ->{
                bloodGroup="AB+"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }
            R.id.abminus ->{
                bloodGroup="AB-"
                Toast.makeText(requireContext(),"You Selected $bloodGroup", Toast.LENGTH_SHORT).show()//single line code
            }

        }
    }


}
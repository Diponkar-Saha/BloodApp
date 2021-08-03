package com.example.splash.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.splash.R
import com.example.splash.databinding.FragmentOneBinding

class OneFragment : Fragment() {
    private var _binding: FragmentOneBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentOneBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)


        binding.next1Btn.setOnClickListener {
            viewPager?.currentItem = 1                     // 0-indexing, 1 is actually the 2nd screen
        }


        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
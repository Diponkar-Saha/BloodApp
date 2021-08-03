package com.example.splash.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.splash.databinding.FragmentViewPagerBinding
import com.example.splash.ui.onboarding.screens.OneFragment
import com.example.splash.ui.onboarding.screens.ThreeFragment
import com.example.splash.ui.onboarding.screens.TwoFragment


class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val view = binding.root



        val fragmentList = arrayListOf<Fragment>(
            OneFragment(),
            TwoFragment(),
            ThreeFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        // Make sure you have the 'kotlin-android-extensions' plugin added to the app.gradle file
        binding.viewPager.adapter = adapter // Depreciated Synthetic view references won't be a problem. Data binding not mandatory.

        return view
    }


}
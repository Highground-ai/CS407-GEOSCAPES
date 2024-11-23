package com.example.geoscapes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.geoscapes.databinding.FragmentLandingPageBinding
import com.example.geoscapes.databinding.FragmentSettingsBinding
import com.example.geoscapes.databinding.FragmentTasksBinding

class LandingPage : Fragment() {
    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLandingPageBinding.inflate(inflater,container,false)



        return binding.root
    }

}
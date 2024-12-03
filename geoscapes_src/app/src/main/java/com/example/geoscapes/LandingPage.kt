package com.example.geoscapes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.geoscapes.databinding.FragmentLandingPageBinding
import com.example.geoscapes.databinding.FragmentSettingsBinding
import com.example.geoscapes.databinding.FragmentTasksBinding

class LandingPage : Fragment() {
    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var goButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLandingPageBinding.inflate(inflater,container,false)

        goButton = _binding!!.goButton
        goButton.setOnClickListener {
            findNavController().navigate(R.id.action_landingPageFragment_to_mapsFragment)
        }
        // TODO: Inflate storyline text

        // TODO: Get current task and display it

        // TODO: If no task, display set task


        return binding.root
    }

}
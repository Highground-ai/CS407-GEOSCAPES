package com.example.geoscapes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class BannerFragment : Fragment() {

    private lateinit var currentTask: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_banner, container, false)

        val helpIcon = view.findViewById<ImageView>(R.id.help_icon)

        currentTask = activity?.getSharedPreferences(
            getString(R.string.currentTaskKey), Context.MODE_PRIVATE)!!

        helpIcon.setOnClickListener {
            TutorialDialogFragment().show(parentFragmentManager, "tutorial")
        }


        return view
    }



}

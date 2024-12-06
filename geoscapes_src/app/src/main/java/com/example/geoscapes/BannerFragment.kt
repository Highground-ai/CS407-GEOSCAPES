package com.example.geoscapes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class BannerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_banner, container, false)

        val questionButton: ImageButton = view.findViewById(R.id.questionButton)
        questionButton.setOnClickListener {
            TutorialDialogFragment().show(parentFragmentManager, "tutorial")
        }

        return view
    }
}

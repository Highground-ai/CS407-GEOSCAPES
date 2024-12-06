package com.example.geoscapes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog

class TutorialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment (optional if you still want to have a layout)
        val view = inflater.inflate(R.layout.tutorial_dialog, container, false)

        // Create and show an AlertDialog
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Tutorial")
            .setMessage("This is a tutorial dialog message.")
            .setPositiveButton("OK") { dialog, which ->
                // Handle OK button click
                Toast.makeText(requireContext(), "OK clicked", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click
                dialog.dismiss()
            }
            .create()

        alertDialog.show()

        return view
    }
}
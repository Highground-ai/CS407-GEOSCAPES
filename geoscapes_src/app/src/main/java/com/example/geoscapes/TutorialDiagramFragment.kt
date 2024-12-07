package com.example.geoscapes

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

class TutorialDialogFragment : DialogFragment() {

    // A list of fragment IDs and corresponding tutorial messages for each step
    private val fragmentSequence = listOf(
        R.id.landingPageFragment to "Welcome to the Landing Page. This is the starting point of the app.",
        R.id.mapsFragment to "Here is the Maps Page. You can explore geographical data.",
        R.id.tasksFragment to "This is the Tasks Page. Manage your tasks here.",
        R.id.settingsFragment to "This is the Settings Page. Configure app preferences here.",
        R.id.landingPageFragment to "Tutorial completed! You are back at the Landing Page."
    )

    // Tracks the current step in the tutorial
    private var currentStep = 0


    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
         AlertDialog.Builder(requireContext())

        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.tutorial_dialog, null)

        val nextButton = dialogView.findViewById<Button>(R.id.button_okay)


        // Create the dialog using the custom view
        return AlertDialog.Builder(requireContext())
            .setView(dialogView) // Set the custom layout as the content
            .setCancelable(false)
            .create()

    }

    // Retrieves the tutorial message for the current step
    private fun getTutorialMessage(): String {
        return fragmentSequence[currentStep].second
    }

    // Navigates to the next fragment and displays the next tutorial dialog
    private fun navigateToNextFragment() {
        if (currentStep < fragmentSequence.size) {
            // Get the destination fragment ID for the current step
            val destinationId = fragmentSequence[currentStep].first

            currentStep++

            // Navigate to the fragment specified by the current step
            findNavController().navigate(destinationId)

            // If there are more steps, show the next tutorial dialog
            if (currentStep < fragmentSequence.size) {
                TutorialDialogFragment().show(parentFragmentManager, "tutorial")
            }
        }
    }
}

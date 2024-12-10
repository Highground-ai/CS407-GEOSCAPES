package com.example.geoscapes

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

class TutorialDialogFragment : DialogFragment() {
    private val fragmentSequence = listOf(
        R.id.landingPageFragment to "Welcome to the Landing Page. This is the starting point of the app.",
        R.id.mapsFragment to "Here is the Maps Page. You can explore geographical data.",
        R.id.tasksFragment to "This is the Tasks Page. Manage your tasks here.",
        R.id.settingsFragment to "This is the Settings Page. Configure app preferences here.",
        R.id.landingPageFragment to "Tutorial completed! You are back at the Landing Page."
    )
    private var currentStep = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setMessage(getTutorialMessage())
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ -> navigateToNextFragment() }
            .create()
    }

    private fun getTutorialMessage(): String {
        return fragmentSequence[currentStep].second
    }

    private fun navigateToNextFragment() {
        if (currentStep < fragmentSequence.size) {
            val destinationId = fragmentSequence[currentStep].first
            currentStep++
            findNavController().navigate(destinationId)

            // Show the next dialog after navigation
            if (currentStep < fragmentSequence.size) {
                TutorialDialogFragment().show(parentFragmentManager, "tutorial")
            }
        }
    }
}

package com.example.geoscapes

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController


class TutorialDialogFragment : DialogFragment() {

    // A list of fragment IDs and corresponding tutorial dialog layouts for each step
    private val fragmentSequence = listOf(
        R.id.landingPageFragment to R.layout.landing_page_dialog,
        R.id.mapsFragment to R.layout.map_page_dialog,
        R.id.tasksFragment to R.layout.tasks_page_dialog,
        R.id.settingsFragment to R.layout.settings_page_dialog,
        R.id.landingPageFragment to R.layout.tutorial_get_started_dialog,
    )

    // Tracks the current step in the tutorial
    private var currentStep = 0

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reset currentStep whenever the tutorial starts
        currentStep = 0
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get the layout resource ID for the current tutorial step
        val dialogLayoutResId = R.layout.tutorial_dialog

        // Inflate the custom layout
        val dialogView = LayoutInflater.from(requireContext()).inflate(dialogLayoutResId, null)

        // Find the OK button in the layout
        val startTutorialButton = dialogView.findViewById<Button>(R.id.button_okay)

        // Create and return the dialog using the inflated view
        val tutorialStartDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView) // Set the custom layout as the content
            .setCancelable(true)
            .create()

        // Set a click listener on the button to navigate to the next fragment
        startTutorialButton.setOnClickListener {
            navigateToNextFragment()
        }

        return tutorialStartDialog

    }

    @SuppressLint("UseGetLayoutInflater")
    private fun navigateToNextFragment() {
        if (currentStep < fragmentSequence.size) {
            val destinationId = fragmentSequence[currentStep].first
            val dialogLayoutResId = fragmentSequence[currentStep].second

            currentStep++

            // Navigate to the next fragment
            findNavController().navigate(destinationId)

            Log.d(
                "TutorialDialogFragment",
                "Navigating to destination: $destinationId for currStep: ${currentStep}"
            )

            // Post the dialog creation after the fragment has been navigated
            Handler(requireContext().mainLooper).postDelayed({
                // Make sure the fragment is still attached before showing the next dialog
                if (isAdded) {
                    // Inflate the custom layout for the next dialog
                    val nextDialogView =
                        LayoutInflater.from(requireContext()).inflate(dialogLayoutResId, null)

                    // Create and show the next dialog
                    val nextDialog = AlertDialog.Builder(requireContext())
                        .setView(nextDialogView)
                        .setCancelable(false)
                        .create()

                    val continueButton = nextDialogView.findViewById<Button>(R.id.button_okay)

                    continueButton.setOnClickListener {
                        nextDialog.dismiss()
                        navigateToNextFragment() // Proceed to the next step
                    }

                    nextDialog.show()
                } else {
                    Log.e(
                        "TutorialDialogFragment",
                        "Fragment is not attached to the FragmentManager"
                    )
                }
            }, 100) // Small delay to allow navigation to complete
        }
    }
}

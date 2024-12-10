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

    // Tracks the current step in the tutorial
    private var currentStep = 0

    // A function to get the current fragment
    private fun getCurrentFragmentId(): Int {
        val navController = findNavController()
        return navController.currentDestination?.id ?: R.id.landingPageFragment // Default if no fragment is found
    }

    // Dynamically generates the fragment sequence
    private fun getFragmentSequence(): List<Pair<Int, Int>> {
        val currentFragmentId = getCurrentFragmentId()

        return listOf(
            currentFragmentId to R.layout.tutorial_dialog,
            R.id.landingPageFragment to R.layout.landing_page_dialog,
            R.id.tasksFragment to R.layout.tasks_page_dialog,
            R.id.mapsFragment to R.layout.map_page_dialog,
            R.id.settingsFragment to R.layout.settings_page_dialog,
            R.id.landingPageFragment to R.layout.tutorial_get_started_dialog,
        )
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Dynamically create the fragment sequence
        val fragmentSequence = getFragmentSequence()

        //must return a dialog in OnCreateDialog to bypass logic
        val tutorialStartDialog = AlertDialog.Builder(requireContext())
            .create()

        navigateToNextFragment(fragmentSequence)

        return tutorialStartDialog
    }

    @SuppressLint("UseGetLayoutInflater")
    private fun navigateToNextFragment(fragmentSequence: List<Pair<Int, Int>>) {
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
                        .apply {
                            if (currentStep == 1) {
                                setCancelable(true) // Make the first dialog cancellable
                            } else {
                                setCancelable(false) // Make subsequent dialogs non-cancellable
                            }
                        }
                        .create()

                    val continueButton = nextDialogView.findViewById<Button>(R.id.button_okay)

                    continueButton.setOnClickListener {
                        nextDialog.dismiss()
                        navigateToNextFragment(fragmentSequence) // Proceed to the next step
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

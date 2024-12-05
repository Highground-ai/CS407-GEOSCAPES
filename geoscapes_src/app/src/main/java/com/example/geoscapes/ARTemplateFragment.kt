package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

/**
 * Template file used to build your new AR tasks off of, I'll be updating this template with
 * code to sync map location (which will be passed in through the map marker) and navigation component
 * and then use that to create a geospatial anchor
 */
class ARTemplateFragment : Fragment(R.layout.fragment_ar_template) {

    private lateinit var arFragment: ArFragment
    private var arSession: Session? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!isARCoreSupportedAndUpToDate(context)) {
            Toast.makeText(context, "ARCore is not supported or needs an update.", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ARFragment
        arFragment = childFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        // Check permissions and initialize AR session
        checkPermissionsAndInitialize()
    }

    private fun isARCoreSupportedAndUpToDate(context: Context): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        return availability.isSupported && !availability.isTransient
    }

    private fun checkPermissionsAndInitialize() {
        if (hasCameraPermission()) {
            initializeARSession()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                initializeARSession()
            } else {
                Toast.makeText(context, "Camera permission is required to use AR features.", Toast.LENGTH_LONG).show()
                activity?.finish()
            }
        }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initializeARSession() {
        try {
            // Create AR session and attach it to the ARFragment
            arSession = Session(requireContext())
            arFragment.arSceneView.setupSession(arSession)

            // Set up tap listener to place objects on detected planes
            arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Add a transformable node (3D object) to the anchor
                val modelNode = TransformableNode(arFragment.transformationSystem)
                modelNode.setParent(anchorNode)
                modelNode.select()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to initialize AR session: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

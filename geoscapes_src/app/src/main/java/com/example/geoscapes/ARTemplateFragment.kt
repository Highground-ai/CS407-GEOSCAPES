package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.navigation.Navigation

class ARTemplateFragment : Fragment(R.layout.fragment_ar_template) {

    // AR Variables
    private lateinit var arFragment: ArFragment
    private var arSession: Session? = null
    private var modelRenderable: ModelRenderable? = null

    // Location Variables
    private var targetLatLng: LatLng? = null
    private var markerTitle: String? = null
    private var markerDescription: String? = null
    private var taskID: Int? = null
    private lateinit var taskDB: TaskDatabase
    private var currTask: Task? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val latitude = it.getDouble("latitude")
            val longitude = it.getDouble("longitude")
            targetLatLng = LatLng(latitude, longitude)
            markerTitle = it.getString("title")
            markerDescription = it.getString("description")
            taskID = it.getInt("taskID")
        }
    }

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
        arFragment = childFragmentManager.findFragmentById(R.id.ARTemplateFragment) as ArFragment

        // Check permissions and initialize AR session
        checkPermissionsAndInitialize()

        // Get the task related to this AR Activity
        job = CoroutineScope(Dispatchers.IO).launch {
            taskDB = TaskDatabase.getDatabase(requireActivity())
            currTask = taskDB.taskDao().getTaskById(taskID!!)
        }
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
            // Create AR session and configure for geospatial mode
            arSession = Session(requireContext())
            val config = arSession!!.config
            config.geospatialMode = Config.GeospatialMode.ENABLED  // Enabling geospatial mode
            arSession!!.configure(config)

            // Directly set up the AR session
            arFragment.arSceneView.session = arSession

            // Optionally, set up AR fragment listeners or callbacks here
            // For instance, for tapping on a plane to place objects:
            /**
            arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)

                // Add a transformable node (3D object) to the anchor
                val modelNode = TransformableNode(arFragment.transformationSystem)
                modelNode.setParent(anchorNode)
                modelNode.select()
            }
            */

        } catch (e: Exception) {
            Toast.makeText(context, "Failed to initialize AR session: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    private fun loadModel() {
        // Load the GLB model from raw resource
        ModelRenderable.builder()
            .setSource(requireContext(), Uri.parse("axe.glb"))
            .build()
            .thenAccept { renderable ->
                modelRenderable = renderable
                Toast.makeText(context, "Model loaded", Toast.LENGTH_SHORT).show()
            }
            .exceptionally { throwable ->
                Toast.makeText(context, "Error loading model: ${throwable.message}", Toast.LENGTH_LONG).show()
                null
            }
    }

    private fun placeGeospatialAnchor() {
        val earth = arSession?.earth
        if (earth == null || targetLatLng == null) {
            Toast.makeText(context, "Earth or target location is not ready.", Toast.LENGTH_SHORT).show()
            return
        }

        if (earth.trackingState != TrackingState.TRACKING) {
            Toast.makeText(context, "Waiting for Earth to start tracking.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create a geospatial anchor
        val latitude = targetLatLng!!.latitude
        val longitude = targetLatLng!!.longitude
        val altitude = earth.cameraGeospatialPose.altitude // Use camera altitude as a base
        val heading = earth.cameraGeospatialPose.eastUpSouthQuaternion // Use current camera heading if necessary

        val anchor = earth.createAnchor(latitude, longitude, altitude, heading)
        if (anchor != null) {
            placeAnchorNode(anchor)
        } else {
            Toast.makeText(context, "Failed to create a geospatial anchor.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun placeAnchorNode(anchor: Anchor) {
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment.arSceneView.scene)

        // Add the model at the anchor
        val modelNode = TransformableNode(arFragment.transformationSystem)
        modelNode.renderable = modelRenderable
        modelNode.setParent(anchorNode)

        // Set click listener to end the activity when the object is clicked
        modelNode.setOnTapListener { _, _ ->
            // TODO: Implement navigation or additional functionality after clicking the model
            Navigation.findNavController(requireView()).navigate(R.id.action_ArTemplateFragment_to_landingFragment)
            onDestroy()
        }

        modelNode.select()
    }

    override fun onResume() {
        super.onResume()
        arFragment.arSceneView.resume()
        arSession?.resume()
    }

    override fun onPause() {
        super.onPause()
        arFragment.arSceneView.pause()
        arSession?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        arSession?.close()
        arSession = null
    }
}

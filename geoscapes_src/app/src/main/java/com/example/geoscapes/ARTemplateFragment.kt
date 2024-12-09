package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.gorisse.thomas.sceneform.scene.await

class ARTemplateFragment : Fragment(R.layout.fragment_ar_template) {

    private lateinit var arFragment: ArFragment
    //private var arSession: Session? = null

    //private var modelRenderer: ModelRenderable? = null
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene

    private var model: Renderable? = null
    private var modelView: ViewRenderable? = null


//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (!isARCoreSupportedAndUpToDate(context)) {
//            Toast.makeText(context, "ARCore is not supported or needs an update.", Toast.LENGTH_LONG).show()
//            activity?.finish()
//        }
//    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionsAndInitialize()
        arFragment = (childFragmentManager.findFragmentById(R.id.ARTemplateFragment) as ArFragment)
            .apply {
                setOnSessionConfigurationListener{ session,config ->
                //modify ar config
                }
                setOnViewCreatedListener  { arSceneView ->
                    arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
                }
                setOnTapArPlaneListener(::onTapPlane)
            }

        lifecycleScope.launchWhenCreated { loadModels()}

        // Check permissions and initialize AR session

    }

    private suspend fun loadModels() {
        model = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/halloween.glb"))
            .setIsFilamentGltf(true)
            .await()
    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (model == null) {
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the Anchor.
        scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
            // Create the transformable model and add it to the anchor.
            addChild(TransformableNode(arFragment.transformationSystem).apply {
                renderable = model
                renderableInstance.setCulling(false)
                renderableInstance.animate(true).start()
                // Add the View
                addChild(Node().apply {
                    // Define the relative position
                    localPosition = Vector3(0.0f, 1f, 0.0f)
                    localScale = Vector3(0.7f, 0.7f, 0.7f)
                    renderable = modelView
                })
            })
        })
    }


    private fun isARCoreSupportedAndUpToDate(context: Context): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        return availability.isSupported && !availability.isTransient
    }

    private fun checkPermissionsAndInitialize() {
        if (hasCameraPermission()) {
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
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



//    private fun initializeARSessionDep() {
//        try {
//            //May Remove Later
//            arSession = Session(requireContext())
//            val config = arSession!!.config.apply {
//                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
//                instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
//                lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
//            }
//            arSession!!.configure(config)
//
//            //wait for fragment to load
//            arFragment.arSceneView.post {
//                //update the session for the fragment
//                //arFragment.arSceneView.session = arSession
//
//                // Load the 3D model
//                loadModel()
//
//                // Set up the tap listener to place the model
//                arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
//                    if (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING && hitResult != null) {
//                        val anchor = hitResult.createAnchor()
//                        placeAnchorNode(anchor)
//                    } else {
//                        Toast.makeText(context, "Please tap on a horizontal plane.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//            // Load the 3D model
//            loadModel()
//
//            // Set up the tap listener to place the model
//            arFragment.setOnTapArPlaneListener { hitResult, plane, _ ->
//                if (plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING) {
//                    val anchor = hitResult.createAnchor()
//                    placeAnchorNode(anchor)
//                } else {
//                    Toast.makeText(context, "Please tap on a horizontal plane.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } catch (e: Exception) {
//            Toast.makeText(context, "Failed to initialize AR session: ${e.message}", Toast.LENGTH_LONG).show()
//            Log.d("Except", e.message!!)
//        }
//
//
//    }

//    private fun loadModelDep() {
//        ModelRenderable.builder()
//            .setSource(requireContext(), Uri.parse("raw/axe.glb")) // Correct path for raw resource
//            .build()
//            .thenAccept { renderable ->
//                modelRenderer = renderable
//                Toast.makeText(context, "Model loaded successfully.", Toast.LENGTH_SHORT).show()
//            }
//            .exceptionally { throwable ->
//                Toast.makeText(context, "Error loading model: ${throwable.message}", Toast.LENGTH_LONG).show()
//                null
//            }
//    }

//    private fun placeAnchorNode(anchor: Anchor) {
//        val anchorNode = AnchorNode(anchor).apply {
//            setParent(arFragment.arSceneView.scene)
//        }
//
//        val modelNode = TransformableNode(arFragment.transformationSystem).apply {
//            renderable = modelRenderer
//            setParent(anchorNode)
//            setOnTapListener { _, _ ->
//                Navigation.findNavController(requireView()).navigate(R.id.action_ArTemplateFragment_to_landingFragment)
//            }
//        }
//
//        modelNode.select()
//    }


}
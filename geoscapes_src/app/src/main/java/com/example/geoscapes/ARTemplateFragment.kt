package com.example.geoscapes

import android.Manifest
import android.R.attr.fragment
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await


/**
 * ARTemplate, used to load the lure bucky activity - allows users to place 3 lures into a scene
 * and after they do, summon bucky.
 */
class ARTemplateFragment : Fragment(R.layout.fragment_ar_template) {

    //Initialize fragment
    private lateinit var arFragment: ArFragment

    //Get the view
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene

    //Create the models
    private var modelLure: Renderable? = null
    private var modelBadger: Renderable? = null
    private var modelView: ViewRenderable? = null

    //Counter to track how many lures
    private var lureCount = 0

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
        modelBadger = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/badger.glb"))
            .setIsFilamentGltf(true)
            .await()
        modelLure = ModelRenderable.builder()
            .setSource(context,Uri.parse("models/axe.glb"))
            .setIsFilamentGltf(true).await()
    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (modelLure == null) {
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            return
        }
        else if (lureCount == 3){
            Toast.makeText(context, "You're out of lures", Toast.LENGTH_SHORT).show()
            getCameraPose()
        }
        else {
            // Create the Anchor.
            scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
                // Create the transformable model and add it to the anchor.
                addChild(TransformableNode(arFragment.transformationSystem).apply {
                    renderable = modelLure
                    renderableInstance.setCulling(false)
                    //renderableInstance.animate(true).start()
                    // Add the View
//                    addChild(Node().apply {
//                        // Define the relative position
//                        localPosition = Vector3(0.0f, 1f, 0.0f)
//                        localScale = Vector3(0.7f, 0.7f, 0.7f)
//                        renderable = modelView
//                    })
                })
            })
            lureCount += 1
        }
    }

    private fun getCameraPose(){
        val frame: Frame = arFragment.arSceneView.arFrame!!
        val camera = frame.camera
        lateinit var CameraPose : Pose
        if (camera.trackingState == TrackingState.TRACKING) {
            CameraPose = camera.pose
            createBadger(CameraPose)
        }

    }

    private fun createBadger(CameraPose : Pose){
        val anchor: Anchor = arSceneView.session?.createAnchor(CameraPose)!!
        val anchorNode = AnchorNode(anchor)
        anchorNode.parent = scene
        anchorNode.addChild(TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelBadger
            renderableInstance.setCulling(false)
            //renderableInstance.animate(true).start()
        })
//        //anchorNode.setRenderable(modelBadger)
//        //anchorNode.setParent(arFragment.arSceneView.scene)
//        scene.addChild(anchorNode).apply {
//            anchorNode.addChild(TransformableNode(arFragment.transformationSystem).apply {
//                renderable = modelBadger
//                renderableInstance.setCulling(false)
//            })
//        }

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





}
package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.gorisse.thomas.sceneform.scene.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch


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
    private var modelViewTwo: ViewRenderable? = null

    //initialize database stuff
    private var job : Job? = null
    private lateinit var taskDB: TaskDatabase
    private lateinit var currentTask: SharedPreferences

    //Counter to track how many lures
    private var lureCount = 0
    private var badgerCount = 0

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionsAndInitialize()
        currentTask = activity?.getSharedPreferences(
            getString(R.string.currentTaskKey), Context.MODE_PRIVATE)!!
        taskDB = TaskDatabase.getDatabase(requireActivity())
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

        modelView = ViewRenderable.builder()
            .setView(context, R.layout.bucky_dialog)
            .build().await()
        modelViewTwo = ViewRenderable.builder()
            .setView(context, R.layout.bucky_dialog_2)
            .build().await()
    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if (modelLure == null) {
            Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
            return
        }else if (badgerCount == 1){
            Toast.makeText(context, "You must capture the beast", Toast.LENGTH_SHORT).show()
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
            CameraPose = camera.displayOrientedPose
            createBadger(CameraPose)
        }
    }



    private fun createBadger(CameraPose : Pose){
        Toast.makeText(context, "The Badger Lurks", Toast.LENGTH_SHORT).show()
        badgerCount += 1
        //TODO: Rotate the badger
        var translation = CameraPose.translation
        var rot = CameraPose.rotationQuaternion
        translation.set(1,translation[1] - 1)
        val anchor: Anchor = arSceneView.session?.createAnchor(Pose(translation,rot))!!
        val anchorNode = AnchorNode(anchor)
        //Navigate away from the badger on tap

        anchorNode.parent = scene
        anchorNode.addChild(TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelBadger
            renderableInstance.setCulling(false)

            setOnTapListener { hitResult: HitTestResult?, motionEvent: MotionEvent? ->
                Toast.makeText(context, "You've Caught The Badger", Toast.LENGTH_SHORT).show()
                //Set Task to Complete
                job = CoroutineScope(Dispatchers.IO).launch {
                    val arTask = taskDB.taskDao().getTaskByName(getString(R.string.fourth_task_name))
                    arTask?.taskCompletion = 100f
                    taskDB.taskDao().upsert(arTask!!)
                    //Set Current Task to None
                    currentTask.edit().putInt("taskID", -1).apply()
                }
                //Navigate to Landing Page
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_ArTemplateFragment_to_landingFragment)
            }

            addChild(Node().apply {
                // Define the relative position
                localPosition = Vector3(0.0f, 1f, 0.0f)
                localScale = Vector3(0.7f, 0.7f, 0.7f)
                renderable = modelView
            })
            addChild(Node().apply {
                // Define the relative position
                localPosition = Vector3(0.0f, 1f, 0.5f)
                localScale = Vector3(0.7f, 0.7f, 0.7f)
                renderable = modelViewTwo
            })
        })
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
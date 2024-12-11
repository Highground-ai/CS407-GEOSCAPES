package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Config
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.SceneView
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
import kotlinx.coroutines.withContext

class GeospatialFragment : Fragment(R.layout.fragment_geospatial) {
    //Initialize fragment
    private lateinit var arFragment: ArFragment

    //Get the view
    private val arSceneView get() = arFragment.arSceneView
    //private val session = arSceneView.session
    private val scene get() = arSceneView.scene

    //Create the models
    private var modelAxe: Renderable? = null
    private var modelBadger: Renderable? = null
    private var modelView: ViewRenderable? = null


    //initialize database stuff
    private var job : Job? = null
    private lateinit var taskDB: TaskDatabase
    private lateinit var currentTask: SharedPreferences
    private lateinit var activeTask: Task

    //Initialize lat and long
    private var lat = 0.0
    private var long = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lat = arguments?.getDouble("latitude")!!
        long = arguments?.getDouble("longitude")!!
        checkPermissionsAndInitialize()
        currentTask = activity?.getSharedPreferences(
            getString(R.string.currentTaskKey), Context.MODE_PRIVATE)!!
        taskDB = TaskDatabase.getDatabase(requireActivity())
        arFragment = (childFragmentManager.findFragmentById(R.id.ARGeospatialFragment) as ArFragment)
            .apply {
                setOnSessionConfigurationListener{ session,config ->
                    //modify ar config
                    session.config.geospatialMode = Config.GeospatialMode.ENABLED
                }
                setOnViewCreatedListener  { arSceneView ->
                    arSceneView.setFrameRateFactor(SceneView.FrameRate.FULL)
                }
            }

        lifecycleScope.launchWhenCreated {
            loadModels()
        }

        // Check permissions and initialize AR session
    }

    private suspend fun loadModels() {
        modelBadger = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/badger.glb"))
            .setIsFilamentGltf(true)
            .await()
        modelAxe = ModelRenderable.builder()
            .setSource(context, Uri.parse("models/axe.glb"))
            .setIsFilamentGltf(true)
            .await()
        modelView = ViewRenderable.builder()
            .setView(context, R.layout.bucky_dialog)
            .build().await()
        placeModels()
    }

    private fun placeModels(){
        Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show()
        val earth = arSceneView.session?.earth

        if (earth != null) {
            if (earth.trackingState!! != TrackingState.TRACKING) {
                Toast.makeText(context, "Loading...", Toast.LENGTH_SHORT).show()
                return
            }else{
                Toast.makeText(context, "Initializing...", Toast.LENGTH_SHORT).show()
                val altitude = earth.cameraGeospatialPose.altitude - 1

                val qtrnX = 0f; val qtrnY = 0f; val qtrnZ = 0f; val qtrnW = 1f;

                val earthAnchor = earth.createAnchor(lat,long,
                    altitude,qtrnX,qtrnY,qtrnZ,qtrnW)
                val anchorNode = AnchorNode(earthAnchor)
                anchorNode.parent = arSceneView.scene
                anchorNode.addChild(TransformableNode(arFragment.transformationSystem).apply {
                    renderable = modelAxe
                    renderableInstance.setCulling(false)
                    setOnTapListener { hitResult: HitTestResult?, motionEvent: MotionEvent? ->
                        Toast.makeText(context, "You've found the axe!", Toast.LENGTH_SHORT).show()
                        //Set Task to Complete
                        job = CoroutineScope(Dispatchers.IO).launch {
                            val arTask = taskDB.taskDao().getTaskByName(getString(R.string.second_task_name))
                            arTask?.taskCompletion = 100f
                            taskDB.taskDao().upsert(arTask!!)
                            //Set Current Task to None
                            currentTask.edit().putInt("taskID", -1).apply()
                        }
                        //Navigate to Landing Page
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_ArTemplateFragment_to_landingFragment)
                    }
                })
            }
        }
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
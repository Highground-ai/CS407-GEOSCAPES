package com.example.geoscapes

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.geoscapes.databinding.FragmentMapsBinding
import com.example.geoscapes.databinding.PopupWindowBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapsFragment : Fragment() {

    private lateinit var client: FusedLocationProviderClient
    private var job : Job? = null
    private lateinit var locationCallback: LocationCallback // Declare locationCallback
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!! // Access the binding safely
    private lateinit var googleMap: GoogleMap //Stores reference to the google map
    private lateinit var settingToggledKV: SharedPreferences // Used to get the settings
    private lateinit var currentTask: SharedPreferences // Used to display location of active
    private lateinit var taskDB: TaskDatabase // Used to access tasks
    private var activeTask: Task? = null // Used to store active task
    private lateinit var userLocation : Location
    private val LOCATION_PERMISSION_REQUEST_CODE = 100 // Unique code for permissions
    private var taskDialog: AlertDialog? = null

    @SuppressLint("PotentialBehaviorOverride")
    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        googleMap.clear()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()

        } else {
            requestLocationPermission()
        }


        // Set marker click listener
        googleMap.setOnMarkerClickListener { marker ->
            //showPopup(marker.title, "This is the description for ${marker.title}")
            marker.showInfoWindow()
            true // Return true to consume the click event
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        currentTask = activity?.getSharedPreferences(
            getString(R.string.currentTaskKey), Context.MODE_PRIVATE)!!
        taskDB = TaskDatabase.getDatabase(requireActivity())

        // Check to see if there is an active task- otherwise create a toast that redirects you to
        // the task page

        // 1) Check Shared Preferences
        val editor: SharedPreferences.Editor = currentTask.edit()
        val taskID = currentTask.getInt("taskID", -1)
        if (taskID != -1){
            // 2) If task exists, then create marker with the specified task
            job = CoroutineScope(Dispatchers.IO).launch {
                activeTask = taskDB.taskDao().getTaskById(taskID)
                withContext(Dispatchers.Main) {
                    activeTask?.let {
                        placeTaskMarkers(it)
                    }
                }
            }
        }
        // 3) else, create a toast and then navigate to the other fragment
        else{
            //TODO: Add alert dialog and auto navigate functionality
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setMessage("It looks like you haven't selected a task, please select a task from" +
                        " the task bar")
                .setTitle("No Task Set")
                .setPositiveButton("Ok") { dialog, which ->
                    findNavController().navigate(R.id.action_mapsFragment_to_tasksFragment)
                }

            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

        }
    }

    /**
     * Show a popup window when a marker is clicked
     */
    private fun showPopup(title: String?, description: String?) {
        val popupBinding = PopupWindowBinding.inflate(LayoutInflater.from(requireContext()))

        // Set title and description
        popupBinding.titleTextView.text = title
        popupBinding.descriptionTextView.text = description
        if (activeTask?.taskCompletion == 0f){
            popupBinding.actionButton.text = "Start Task"
        } else {
            popupBinding.actionButton.text = "Continue Task"
        }


        //TODO:
        // Set up button functionality, should navigate to the AR_template activity
        // Using the specified location, task information, and activity ID - however this button
        // should only be clickable when the user location is within the radius (i.e, checkRadius
        // function, else it should make a toast that says user isn't with the specified radius.
        popupBinding.actionButton.setOnClickListener {
            val bundle = Bundle().apply {
                putDouble("latitude", activeTask!!.location.latitude)
                putDouble("longitude", activeTask!!.location.longitude)
                putString("title", title)
                putInt("TaskID", activeTask!!.taskId)
                putString("description", description)
            }
            Navigation.findNavController(requireView())
                .navigate(R.id.action_mapsFragment_to_arTemplateFragment, bundle)
            taskDialog?.dismiss()
        }
        // Shows the dialog if it is not already showing
        if (taskDialog == null || !taskDialog!!.isShowing) {
            taskDialog = AlertDialog.Builder(requireContext())
                .setView(popupBinding.root)
                .create()
            taskDialog!!.show()
        }

    }


    /**
     * Used to start location tracking when permission is granted
     */
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            startLocationUpdates()
        }
    }

    /**
     * Used to ask for location Permissions
     */
    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Method used to track user location
     */
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // Update interval in milliseconds
        ).setMinUpdateIntervalMillis(5000L).build()

        locationCallback = object : LocationCallback() { // Initialize locationCallback here
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                userLocation = locationResult.lastLocation!!
                if (userLocation != null) {
                    val currentLatLng = LatLng(userLocation.latitude, userLocation.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    if (currentTask.getInt("taskID", -1) != -1) {
                        if (checkRadius(currentLatLng, activeTask!!.location, activeTask!!.radius)) {
                            showPopup(activeTask!!.taskName, activeTask!!.taskDescription)
                        }
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            client.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    /**
     * Used to set user Permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
                settingToggledKV.edit().putBoolean(getString(R.string.setting_location), true).apply()
            } else {
                Toast.makeText(requireContext(), "Location permission required", Toast.LENGTH_SHORT)
                    .show()
                settingToggledKV.edit().putBoolean(getString(R.string.setting_location), false).apply()
            }
        }
    }

    /**
     * Helper method used to quickly add markers to map
     */
    private fun placeTaskMarkers(
        task : Task
    ){
        val taskPosition = task.location
        val taskTitle = task.taskName
        var newMarker = googleMap.addMarker(
            MarkerOptions().position(taskPosition).title(taskTitle)
        )
        val circle = googleMap.addCircle(CircleOptions()
            .center(taskPosition)
            .radius(task.radius.toDouble())
            .fillColor(Color.argb(128, 255, 0, 0))
            .strokeColor(ContextCompat.getColor(requireContext(), R.color.red)))

        newMarker?.tag = task
    }

    /**
     * Helper Method to see if user is within radius of marker
     */
    private fun checkRadius(
        start: LatLng,
        end: LatLng,
        radius: Int
    ): Boolean {
        val dist = SphericalUtil.computeDistanceBetween(start, end)
        if (dist > radius){
            return false;
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::locationCallback.isInitialized) {
            client.removeLocationUpdates(locationCallback)
        }
    }
}

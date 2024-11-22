package com.example.geoscapes

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.geoscapes.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDB: TaskDatabase
    private var job : Job? = null
    private lateinit var settingToggledKV: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        settingToggledKV = this.getSharedPreferences(
            getString(R.string.settingToggledKV), Context.MODE_PRIVATE
        )
        if (settingToggledKV.getBoolean("Dark Mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        taskDB = TaskDatabase.getDatabase(this)

        job = CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..20) {
                taskDB.deleteDao().delete(i)
            }
            taskDB.taskDao().upsert(Task(taskId=1,taskName="Test", taskDescription = null, taskCompletion=0f))
            val testTask = taskDB.taskDao().getTaskByName("Test")
            if (testTask != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step",
                    stepDescription = null,
                    stepCompletion = false,
                    location = LatLng(0.0, 0.0)),
                    testTask.taskId)
            }
            taskDB.taskDao().upsert(Task(taskId=2,taskName="Test2", taskDescription = null, taskCompletion=100f))
            val testTask2 = taskDB.taskDao().getTaskByName("Test2")
            if (testTask2 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 2",
                    stepDescription = null,
                    stepCompletion = true,
                    location = LatLng(0.0, 0.0)),
                    testTask2.taskId)
            }
            taskDB.taskDao().upsert(Task(taskId=3,taskName="Test3", taskDescription = null, taskCompletion=50f))
            val testTask3 = taskDB.taskDao().getTaskByName("Test3")
            if (testTask3 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 3",
                    stepDescription = null,
                    stepCompletion = true,
                    location = LatLng(0.0, 0.0)),
                    testTask3.taskId)
            }
            if (testTask3 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 4",
                    stepDescription = null,
                    stepCompletion = false,
                    location = LatLng(0.0, 0.0)),
                    testTask3.taskId)
            }
        }
    }
}
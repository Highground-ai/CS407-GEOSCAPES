package com.example.geoscapes

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.view.View
import android.widget.ImageView
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mlText: String = "" // Text from readText, is "No text found" if no text is found
    private var mlLabels: List<String> = MutableList(0) {""} // List of labels from readLabels, first element is "No labels found" if no labels are found
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
        if (settingToggledKV.getBoolean(getString(R.string.setting_dark_mode), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        taskDB = TaskDatabase.getDatabase(this)

        job = CoroutineScope(Dispatchers.IO).launch {
            if (taskDB.taskDao().getTaskByName("Test") != null) {
                for (task in taskDB.taskDao().getAllTasks()) {
                    taskDB.deleteDao().delete(task.taskId)
                }
            }
            if (taskDB.taskDao().getAllTasks().isEmpty()) {
                createTasks()
            }
        }
    }

    // Reads text from the image and appends mlText with the text in it
    fun readText(inputImage: ImageView){
        val bitmap = (inputImage.drawable as BitmapDrawable).bitmap
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = TextRecognizerOptions.DEFAULT_OPTIONS
        val recognizer: TextRecognizer = TextRecognition.getClient(options)
        mlText = ""
        recognizer.process(image)
            //executed when text recognition is successful
            .addOnSuccessListener { visionText ->
                if (visionText.textBlocks.isEmpty()) {
                    mlText = "No text found"
                } else {
                    for (block in visionText.textBlocks) {
                        mlText += block.text
                    }
                }
            }
            .addOnFailureListener {
                // Failed to recognize text
            }
    }

    fun readLabels(inputImage: ImageView) {
        // TODO: Implement the Basic Setup For Label Recognition
        val bitmap = (inputImage.drawable as BitmapDrawable).bitmap
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = ImageLabelerOptions.DEFAULT_OPTIONS
        val labeler: ImageLabeler = ImageLabeling.getClient(options)
        mlLabels = emptyList()
        // TODO: Add Listeners for Label detection process
        labeler.process(image).addOnSuccessListener { labels ->
            if (labels.isEmpty()) {
                mlLabels += "No labels found"
            }
            for (label in labels) {
                mlLabels += label.text
            }
        }.addOnFailureListener {
            // Failed to detect labels
        }
    }

    private suspend fun createTasks() {
        // First task: A Hunter’s Tools
        taskDB.taskDao().upsert(Task(taskId=1,taskName=getString(R.string.first_task_name),
            taskDescription = getString(R.string.first_task_description),
            storyline = getString(R.string.first_task_storyline)))
        val firstTask = taskDB.taskDao().getTaskByName(getString(R.string.first_task_name))

        // Second Task: A Badger’s Shame
        taskDB.taskDao().upsert(Task(
            taskName = getString(R.string.second_task_name),
            taskDescription = getString(R.string.second_task_description),
            location = LatLng(43.0711021300514, -89.4093681166184), // Arch outside camp randall
            radius = 100,
            storyline = getString(R.string.second_task_storyline))
        )
        val secondTask = taskDB.taskDao().getTaskByName(getString(R.string.second_task_name))
        if (secondTask != null) {
            if (taskDB.stepDao().getByName(getString(R.string.second_task_step_one_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.second_task_step_one_name),
                    activityId=null),
                    secondTask.taskId)
            }
            if (taskDB.stepDao().getByName(getString(R.string.second_task_step_two_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.second_task_step_two_name),
                    activityId=null),
                    secondTask.taskId)
            }
        }

        // Third Task: A Badger’s Origin
        taskDB.taskDao().upsert(Task(
            taskName=getString(R.string.third_task_name),
            taskDescription = getString(R.string.third_task_description),
            radius = 50,
            location = LatLng(43.07532181449608, -89.40369435577482),
            storyline = getString(R.string.third_task_storyline)))

        val thirdTask = taskDB.taskDao().getTaskByName(getString(R.string.third_task_name))
        if (thirdTask != null) {
            if (taskDB.stepDao().getByName(getString(R.string.third_task_step_one_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.third_task_step_one_name),
                    activityId=null), thirdTask.taskId)
            }
            if (taskDB.stepDao().getByName(getString(R.string.third_task_step_two_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.third_task_step_two_name),
                    activityId=null), thirdTask.taskId)
            }
        }

        // Fourth Task: A Badger’s Escape
        taskDB.taskDao().upsert(Task(
            taskName=getString(R.string.fourth_task_name),
            taskDescription = getString(R.string.fourth_task_description),
            radius = 100,
            location = LatLng(43.076282453635386, -89.39986102675734),
            storyline = getString(R.string.fourth_task_storyline)))
        val fourthTask = taskDB.taskDao().getTaskByName(getString(R.string.fourth_task_name))
        if (fourthTask != null) {
            if (taskDB.stepDao().getByName(getString(R.string.fourth_task_step_one_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.fourth_task_step_one_name),
                    activityId=null), fourthTask.taskId)
            }
            if (taskDB.stepDao().getByName(getString(R.string.fourth_task_step_two_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.fourth_task_step_two_name),
                    activityId=null), fourthTask.taskId)
            }
            if (taskDB.stepDao().getByName(getString(R.string.fourth_task_step_three_name)) == null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName=getString(R.string.fourth_task_step_three_name),
                    activityId=null), fourthTask.taskId)
            }
        }
    }
}
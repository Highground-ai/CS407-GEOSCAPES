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
            for (i in 1..20) {
                taskDB.deleteDao().delete(i)
            }
            taskDB.taskDao().upsert(Task(taskId=1,taskName="Test",
                taskDescription = null,
                taskCompletion=0f,
                location = LatLng(43.071610705117166, -89.39538308007549),
                radius = 200,
                storyline = "This is a line of story for Test"))
            val testTask = taskDB.taskDao().getTaskByName("Test")
            if (testTask != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step",
                    stepDescription = null,
                    stepCompletion = false,
                    activityId=null),
                    testTask.taskId)
            }
            taskDB.taskDao().upsert(Task(
                    taskId = 2,
                    taskName = "Test2",
                    taskDescription = null,
                    taskCompletion = 100f,
                    location = LatLng(43.0746930639629, -89.41074114655),
                    storyline = "This is a line of story for Test2")
            )
            val testTask2 = taskDB.taskDao().getTaskByName("Test2")
            if (testTask2 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 2",
                    stepDescription = null,
                    stepCompletion = true,
                    activityId=null),
                    testTask2.taskId)
            }
            taskDB.taskDao().upsert(Task(
                taskId=3,taskName="Test3",
                taskDescription = null,
                taskCompletion=50f,
                location = LatLng(43.0766930639629, -89.41004114655),
                storyline = "This is a line of story for Test3"))

            val testTask3 = taskDB.taskDao().getTaskByName("Test3")
            if (testTask3 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 3",
                    stepDescription = null,
                    stepCompletion = true,
                    activityId=null,
                    ),
                    testTask3.taskId)
            }
            if (testTask3 != null) {
                taskDB.stepDao().upsertStep(Step(
                    stepName="Test Step 4",
                    stepDescription = null,
                    stepCompletion = false,
                    activityId=null),
                    testTask3.taskId)
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
    }
}
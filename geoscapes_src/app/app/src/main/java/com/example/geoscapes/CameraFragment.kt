package com.example.geoscapes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.geoscapes.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.InputStream

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var photoImageView: ImageView
    private lateinit var textOutput: TextView
    private lateinit var recognizer: TextRecognizer
    private lateinit var locations: List<Location>

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 200
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoImageView = view.findViewById(R.id.photoImageView)
        textOutput = view.findViewById(R.id.textOutput)

        val selectPhotoButton: Button = view.findViewById(R.id.selectPhotoButton)
        val takePhotoButton: Button = view.findViewById(R.id.takePhotoButton)

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        locations = loadLocationsFromJson()

        selectPhotoButton.setOnClickListener {
            openGallery()
        }

        takePhotoButton.setOnClickListener {
            openCamera()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                textOutput.text = ""

                val detectedText = StringBuilder()
                for (block in visionText.textBlocks) {
                    detectedText.append(block.text).append(" ")
                }

                val matchedLocations = locations.filter { location ->
                    detectedText.toString().contains(location.referenceText, ignoreCase = true)
                }

                if (matchedLocations.isNotEmpty()) {
                    textOutput.text = "Matched Locations:\n"
                    matchedLocations.forEach { location ->
                        textOutput.append("${location.name}\n")
                    }
                } else {
                    textOutput.text = "No matching locations found."
                }
            }
            .addOnFailureListener { e ->
                textOutput.text = "Error: ${e.localizedMessage}"
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val imageUri = data?.data
                    photoImageView.setImageURI(imageUri)

                    val drawable = photoImageView.drawable as? BitmapDrawable
                    val bitmap = drawable?.bitmap
                    if (bitmap != null) {
                        recognizeTextFromImage(bitmap)
                    } else {
                        textOutput.text = "Failed to load image."
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        photoImageView.setImageBitmap(bitmap)
                        recognizeTextFromImage(bitmap)
                    } else {
                        textOutput.text = "Failed to capture image."
                    }
                }
            }
        }
    }

    private fun loadLocationsFromJson(): List<Location> {
        val locationList = mutableListOf<Location>()
        val inputStream: InputStream = resources.openRawResource(R.raw.locations)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val name = jsonObject.getString("name")
            val referenceText = jsonObject.getString("referenceText")
            locationList.add(Location(name, referenceText))
        }

        return locationList
    }

    private data class Location(
        val name: String,
        val referenceText: String
    )
}

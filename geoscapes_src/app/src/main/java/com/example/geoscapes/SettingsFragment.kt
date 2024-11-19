package com.example.geoscapes

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.geoscapes.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsRecyclerView: RecyclerView
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var settingToggledKV: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)

        settingsRecyclerView = _binding!!.settingsRecyclerView
        settingToggledKV = requireContext().getSharedPreferences(
            getString(R.string.settingToggledKV), Context.MODE_PRIVATE
        )
        if (settingToggledKV.getBoolean("Dark Mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        // Gets settings from sharedPreferences and creates a list of SettingItem objects
        // Not sure if the user updating permissions outside app will impact this
        val settingsList = listOf(
            SettingItem("Dark Mode", settingToggledKV.getBoolean("Dark Mode", false)),
            SettingItem("Music", settingToggledKV.getBoolean("Music", true)),
            SettingItem("SFX", settingToggledKV.getBoolean("SFX", true)),
            SettingItem("Haptic Feedback", settingToggledKV.getBoolean("Haptic Feedback", true)),
            SettingItem("Notifications", settingToggledKV.getBoolean("Notifications", false)),
            SettingItem("Camera", settingToggledKV.getBoolean("Camera", false)),
            SettingItem("Microphone", settingToggledKV.getBoolean("Microphone", false)),
            SettingItem("Location", settingToggledKV.getBoolean("Location", false))
        )

        settingsAdapter = SettingsAdapter(settingsList) { settingItem, isChecked ->
            // Handle the switch toggle event
            // TODO Switch the switch back off if the user denies the permission
            when (settingItem.title) {
                "Location" -> {
                    if (isChecked && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this.requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                        )
                    }
                }

                "Camera" -> {
                    if (isChecked && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this.requireActivity(),
                            arrayOf(Manifest.permission.CAMERA), 1
                        )
                    }
                }

                "Microphone" -> {
                    if (isChecked && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.RECORD_AUDIO
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this.requireActivity(),
                            arrayOf(Manifest.permission.RECORD_AUDIO), 1
                        )
                    }
                }

                "Notifications" -> {
                    if (isChecked && ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this.requireActivity(),
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
                        )
                    }
                }

                "Dark Mode" -> {
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
            // Update sharedPreferences for the item that was checked
            settingToggledKV.edit().putBoolean(settingItem.title, isChecked).apply()
        }

        settingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = settingsAdapter
        }

        return binding.root
    }
}
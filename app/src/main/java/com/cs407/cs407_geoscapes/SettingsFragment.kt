package com.cs407.cs407_geoscapes

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var settingsRecyclerView: RecyclerView
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var settingToggledKV: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsRecyclerView = view.findViewById(R.id.settings_recycler_view)
        settingToggledKV = requireContext().getSharedPreferences(
            getString(R.string.settingToggledKV), Context.MODE_PRIVATE)
        // List of setting options and default states
        val settingsList = listOf(
            SettingItem("Dark Mode", settingToggledKV.getBoolean("Dark Mode", false)),
            SettingItem("Music", settingToggledKV.getBoolean("Music", true)),
            SettingItem("SFX", settingToggledKV.getBoolean("SFX", true)),
            SettingItem("Haptic Feedback", settingToggledKV.getBoolean("Haptic Feedback", true)),
            SettingItem("Notifications", settingToggledKV.getBoolean("Notifications", false)),
            SettingItem("Camera", settingToggledKV.getBoolean("Camera", false)),
            SettingItem("Microphone", settingToggledKV.getBoolean("Microphone", false))
        )

        settingsAdapter = SettingsAdapter(settingsList) { settingItem, isChecked ->
            // Handle the switch toggle event
            settingItem.isChecked = isChecked
            // Update sharedPreferences
            settingToggledKV.edit().putBoolean(settingItem.title, isChecked).apply()
        }

        settingsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = settingsAdapter
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
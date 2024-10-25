package com.cs407.cs407_geoscapes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
/**
 * Adapter for the settings RecyclerView.
 * Takes in a list of SettingItem and a callback for when the switch is toggled.
 */
class SettingsAdapter(
    private val settingsList: List<SettingItem>,
    private val onSwitchToggled: (SettingItem, Boolean) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_setting, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val settingItem = settingsList[position]
        holder.bind(settingItem)
    }

    override fun getItemCount(): Int = settingsList.size

    inner class SettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.setting_title)
        private val switchView: Switch = itemView.findViewById(R.id.setting_switch)

        fun bind(settingItem: SettingItem) {
            titleTextView.text = settingItem.title
            switchView.isChecked = settingItem.isChecked

            // Update the checked state and invoke the callback when toggled
            switchView.setOnCheckedChangeListener { _, isChecked ->
                settingItem.isChecked = isChecked
                onSwitchToggled(settingItem, isChecked)
            }
        }
    }
}

package com.example.geoscapes

// data class for each setting item, only supports boolean for now, can add drawable for icons later
data class SettingItem(
    val title: String,
    var isChecked: Boolean
)

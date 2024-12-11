package com.example.geoscapes

data class Location(
    val name: String,
    val coords: Pair<Double, Double>,
    val referenceText: String
)

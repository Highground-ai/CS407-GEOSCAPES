package com.example.geoscapes

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class TasksFragmentDirections private constructor() {
  public companion object {
    public fun actionTasksFragmentToMapsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_tasksFragment_to_mapsFragment)
  }
}

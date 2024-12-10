package com.example.geoscapes

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class MapsFragmentDirections private constructor() {
  public companion object {
    public fun actionMapsFragmentToTasksFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_mapsFragment_to_tasksFragment)
  }
}

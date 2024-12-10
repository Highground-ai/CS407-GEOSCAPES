package com.example.geoscapes

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class LandingPageDirections private constructor() {
  public companion object {
    public fun actionLandingPageFragmentToMapsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_landingPageFragment_to_mapsFragment)

    public fun actionLandingPageFragmentToTasksFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_landingPageFragment_to_tasksFragment)
  }
}

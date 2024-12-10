package com.example.geoscapes

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class CameraFragmentDirections private constructor() {
  public companion object {
    public fun actionCameraFragmentToLandingPageFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_cameraFragment_to_landingPageFragment)
  }
}

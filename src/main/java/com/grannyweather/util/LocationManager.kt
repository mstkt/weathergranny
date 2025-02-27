package com.grannyweather.util

import android.content.Context
import javax.inject.Inject

class LocationManager @Inject constructor(
    private val context: Context
) {
    // TODO: Implement location services
}

sealed class LocationPermissionState {
    object Unknown : LocationPermissionState()
    object Granted : LocationPermissionState()
    object Denied : LocationPermissionState()
    object RequiresPermission : LocationPermissionState()
} 
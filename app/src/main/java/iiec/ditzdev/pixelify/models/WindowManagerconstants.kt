package iiec.ditzdev.pixelify.models

object WindowManagerConstants {
    object WindowManagerGlobal {
        const val CLASS_NAME = "android.view.WindowManagerGlobal"
        const val GET_WINDOW_MANAGER_SERVICE = "getWindowManagerService"
    }

    object IWindowManager {
        const val CLASS_NAME = "android.view.IWindowManager"
        const val SET_FORCED_DISPLAY_SIZE = "setForcedDisplaySize"
        const val CLEAR_FORCED_DISPLAY_SIZE = "clearForcedDisplaySize"
        const val SET_FORCED_DISPLAY_DENSITY = "setForcedDisplayDensity"
        const val SET_FORCED_DISPLAY_DENSITY_FOR_USER = "setForcedDisplayDensityForUser"
        const val CLEAR_FORCED_DISPLAY_DENSITY = "clearForcedDisplayDensity"
        const val CLEAR_FORCED_DISPLAY_DENSITY_FOR_USER = "clearForcedDisplayDensityForUser"
        const val GET_INITIAL_DISPLAY_SIZE = "getInitialDisplaySize"
        const val GET_INITIAL_DISPLAY_DENSITY = "getInitialDisplayDensity"
    }
}
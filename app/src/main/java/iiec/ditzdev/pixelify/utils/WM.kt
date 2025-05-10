package iiec.ditzdev.pixelify.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Display
import iiec.ditzdev.pixelify.models.WindowManagerConstants

@SuppressLint("PrivateApi")
class WM(contentResolver: ContentResolver) {
    companion object {
        private const val USER_ID = -3

        // Settings keys to unblock hidden APIs
        private val GLOBAL_SETTINGS_BLACKLIST_KEYS = arrayOf(
            "hidden_api_policy",
            "hidden_api_policy_pre_p_apps",
            "hidden_api_policy_p_apps"
        )
    }

    private val iWindowManager: Any

    /**
     * Constructor that initializes access to IWindowManager and unblocks private APIs.
     *
     * @param contentResolver The ContentResolver to modify global settings
     * @throws Exception if initialization fails
     */
    init {
        for (key in GLOBAL_SETTINGS_BLACKLIST_KEYS) {
            Settings.Global.putInt(contentResolver, key, 1)
        }

        val windowManagerGlobalClass = Class.forName(WindowManagerConstants.WindowManagerGlobal.CLASS_NAME)
        val getWindowManagerServiceMethod = windowManagerGlobalClass.getMethod(
            WindowManagerConstants.WindowManagerGlobal.GET_WINDOW_MANAGER_SERVICE
        )
        iWindowManager = getWindowManagerServiceMethod.invoke(null)!!
    }

    /**
     * Sets a custom resolution for the display.
     *
     * @param x Width in pixels
     * @param y Height in pixels
     * @throws Exception if method access fails
     */
    fun setResolution(x: Int, y: Int) {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)
        val setForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_SIZE,
            Int::class.java, Int::class.java, Int::class.java
        )
        setForcedDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, x, y)
    }

    /**
     * Gets the real resolution of the display from system defaults.
     *
     * @return A Point containing width and height
     * @throws Exception if method access fails
     */
    fun getRealResolution(): Point {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)
        val getInitialDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_SIZE,
            Int::class.java, Point::class.java
        )
        val point = Point()
        getInitialDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, point)
        return point
    }

    /**
     * Clears the custom resolution and restores default resolution.
     *
     * @throws Exception if method access fails
     */
    fun clearResolution() {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)
        val clearForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_SIZE,
            Int::class.java
        )
        clearForcedDisplaySizeMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY)
    }

    /**
     * Sets a custom display density (DPI).
     *
     * @param density The desired DPI value
     * @throws Exception if method access fails
     */
    fun setDisplayDensity(density: Int) {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            val setForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY,
                Int::class.java, Int::class.java
            )
            setForcedDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, density)
        } else {
            val setForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY_FOR_USER,
                Int::class.java, Int::class.java, Int::class.java
            )
            setForcedDisplayDensityForUserMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, density, USER_ID)
        }
    }

    /**
     * Clears the custom display density and restores default density.
     *
     * @throws Exception if method access fails
     */
    fun clearDisplayDensity() {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            val clearForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY,
                Int::class.java
            )
            clearForcedDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY)
        } else {
            val clearForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY_FOR_USER,
                Int::class.java, Int::class.java
            )
            clearForcedDisplayDensityForUserMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY, USER_ID)
        }
    }

    /**
     * Retrieves the real (default) display density of the system.
     *
     * @return The density in DPI
     * @throws Exception if method access fails
     */
    fun getRealDensity(): Int {
        val iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME)
        val getInitialDisplayDensityMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_DENSITY,
            Int::class.java
        )
        return getInitialDisplayDensityMethod.invoke(iWindowManager, Display.DEFAULT_DISPLAY) as Int
    }
}
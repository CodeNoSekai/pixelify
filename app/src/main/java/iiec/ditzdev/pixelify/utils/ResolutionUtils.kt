package iiec.ditzdev.pixelify.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.abs

object ResolutionUtils {
    data class DeviceResolution(
        val width: Int,
        val height: Int,
        val dpi: Int,
        val scaleFactor: Float
    )

    fun getDefaultResolution(context: Context): DeviceResolution {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return DeviceResolution(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            displayMetrics.densityDpi,
            displayMetrics.density
        )
    }

    fun isResolutionDangerous(defaultRes: DeviceResolution, newWidth: Int, newHeight: Int, newDpi: Int): Boolean {
        val widthDiff = abs(newWidth.toDouble() / defaultRes.width - 1) * 100
        val heightDiff = abs(newHeight.toDouble() / defaultRes.height - 1) * 100
        val dpiDiff = abs(newDpi.toDouble() / defaultRes.dpi - 1) * 100

        return widthDiff > 50 || heightDiff > 50 || dpiDiff > 50
    }
}
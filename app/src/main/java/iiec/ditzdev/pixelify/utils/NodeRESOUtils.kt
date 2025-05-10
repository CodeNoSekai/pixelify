package iiec.ditzdev.pixelify.utils

import android.graphics.Point
import iiec.ditzdev.pixelify.models.Resolution
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class NodeRESOUtils(windowManager: WM) {
    private val realResolution: Resolution
    private val resolutionDivisor: Double

    init {
        val realPoint = windowManager.getRealResolution()
        this.realResolution = Resolution(realPoint.x, realPoint.y)
        this.resolutionDivisor = getDiagonalPixels(realResolution) / windowManager.getRealDensity()
    }

    /**
     * Calculate diagonal pixel width
     */
    private fun getDiagonalPixels(resolution: Resolution): Double {
        return sqrt(
            resolution.width.toDouble().pow(2) +
                    resolution.height.toDouble().pow(2)
        )
    }

    /**
     * Calculate DPI for a given resolution
     */
    fun getDPI(resolution: Resolution): Int {
        val diagonalPixels = getDiagonalPixels(resolution)
        return round(diagonalPixels / resolutionDivisor).toInt()
    }

    /**
     * Scale resolution by percentage
     */
    fun scaleResolution(resolution: Resolution, percent: Float): Resolution {
        val scale = percent / 100f
        return Resolution(
            round(resolution.width * scale).toInt(),
            round(resolution.height * scale).toInt()
        )
    }

    /**
     * Get the real resolution
     */
    fun getRealResolution(): Resolution {
        return realResolution
    }
}

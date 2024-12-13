package node.ditzdev.pixelify.utils;

import android.graphics.Point;
import node.ditzdev.pixelify.models.Resolution;

public class NodeRESOUtils {
    private final Resolution realResolution;
    private final double resolutionDivisor;

    public NodeRESOUtils(WM windowManager) throws Exception {
        Point realPoint = windowManager.getRealResolution();
        this.realResolution = new Resolution(realPoint.x, realPoint.y);
        this.resolutionDivisor = getDiagonalPixels(realResolution) / windowManager.getRealDensity();
    }

    /**
     * Calculate diagonal pixel width
     */
    private double getDiagonalPixels(Resolution resolution) {
        return Math.sqrt(
            Math.pow(resolution.getWidth(), 2) + 
            Math.pow(resolution.getHeight(), 2)
        );
    }

    /**
     * Calculate DPI for a given resolution
     */
    public int getDPI(Resolution resolution) {
        double diagonalPixels = getDiagonalPixels(resolution);
        return (int) Math.round(diagonalPixels / resolutionDivisor);
    }

    /**
     * Scale resolution by percentage
     */
    public Resolution scaleResolution(Resolution resolution, float percent) {
        float scale = percent / 100f;
        return new Resolution(
            Math.round(resolution.getWidth() * scale),
            Math.round(resolution.getHeight() * scale)
        );
    }

    /**
     * Get the real resolution
     */
    public Resolution getRealResolution() {
        return realResolution;
    }
}
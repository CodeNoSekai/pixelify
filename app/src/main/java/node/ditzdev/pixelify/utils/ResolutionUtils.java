package node.ditzdev.pixelify.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ResolutionUtils {
    public static class DeviceResolution {
        public int width;
        public int height;
        public int dpi;
        public float scaleFactor;

        public DeviceResolution(int width, int height, int dpi, float scaleFactor) {
            this.width = width;
            this.height = height;
            this.dpi = dpi;
            this.scaleFactor = scaleFactor;
        }
    }

    public static DeviceResolution getDefaultResolution(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        return new DeviceResolution(
            displayMetrics.widthPixels,
            displayMetrics.heightPixels,
            displayMetrics.densityDpi,
            displayMetrics.density
        );
    }

    public static boolean isResolutionDangerous(DeviceResolution defaultRes, int newWidth, int newHeight, int newDpi) {
        double widthDiff = Math.abs((double)newWidth / defaultRes.width - 1) * 100;
        double heightDiff = Math.abs((double)newHeight / defaultRes.height - 1) * 100;
        double dpiDiff = Math.abs((double)newDpi / defaultRes.dpi - 1) * 100;

        return widthDiff > 50 || heightDiff > 50 || dpiDiff > 50;
    }
}
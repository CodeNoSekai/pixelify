package node.ditzdev.pixelify.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;

import java.lang.reflect.Method;

import node.ditzdev.pixelify.models.Resolution;
import node.ditzdev.pixelify.models.WindowManagerConstants;

public class WM {
    private static final int USER_ID = -3;
    private static final String[] GLOBAL_SETTINGS_BLACKLIST_KEYS = {
        "hidden_api_policy",
        "hidden_api_policy_pre_p_apps",
        "hidden_api_policy_p_apps"
    };

    private final Object iWindowManager;

    @SuppressLint("PrivateApi")
    public WM(ContentResolver contentResolver) throws Exception {
        // Unblock private APIs
        for (String key : GLOBAL_SETTINGS_BLACKLIST_KEYS) {
            Settings.Global.putInt(contentResolver, key, 1);
        }
        
       /**
         * Resolve Window Manager
         */
        Class<?> windowManagerGlobalClass = Class.forName(WindowManagerConstants.WindowManagerGlobal.CLASS_NAME);
        Method getWindowManagerServiceMethod = windowManagerGlobalClass.getMethod(
            WindowManagerConstants.WindowManagerGlobal.GET_WINDOW_MANAGER_SERVICE
        );
        
        iWindowManager = getWindowManagerServiceMethod.invoke(null);
    }

    @SuppressLint("PrivateApi")
    public void setResolution(int x, int y) throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method setForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_SIZE,
            int.class, int.class, int.class
        );
        
        setForcedDisplaySizeMethod.invoke(
            iWindowManager, 
            Display.DEFAULT_DISPLAY, 
            x, 
            y
        );
    }

    @SuppressLint("PrivateApi")
    public Point getRealResolution() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method getInitialDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_SIZE,
            int.class, Point.class
        );
        
        Point point = new Point();
        getInitialDisplaySizeMethod.invoke(
            iWindowManager, 
            Display.DEFAULT_DISPLAY, 
            point
        );
        
        return point;
    }

    @SuppressLint("PrivateApi")
    public void clearResolution() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method clearForcedDisplaySizeMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_SIZE,
            int.class
        );
        
        clearForcedDisplaySizeMethod.invoke(
            iWindowManager, 
            Display.DEFAULT_DISPLAY
        );
    }

    @SuppressLint("PrivateApi")
    public void setDisplayDensity(int density) throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            Method setForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY,
                int.class, int.class
            );
            
            setForcedDisplayDensityMethod.invoke(
                iWindowManager, 
                Display.DEFAULT_DISPLAY, 
                density
            );
        } else {
            Method setForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.SET_FORCED_DISPLAY_DENSITY_FOR_USER,
                int.class, int.class, int.class
            );
            
            setForcedDisplayDensityForUserMethod.invoke(
                iWindowManager, 
                Display.DEFAULT_DISPLAY, 
                density, 
                USER_ID
            );
        }
    }

    @SuppressLint("PrivateApi")
    public void clearDisplayDensity() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            Method clearForcedDisplayDensityMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY,
                int.class
            );
            
            clearForcedDisplayDensityMethod.invoke(
                iWindowManager, 
                Display.DEFAULT_DISPLAY
            );
        } else {
            Method clearForcedDisplayDensityForUserMethod = iWindowManagerClass.getMethod(
                WindowManagerConstants.IWindowManager.CLEAR_FORCED_DISPLAY_DENSITY_FOR_USER,
                int.class, int.class
            );
            
            clearForcedDisplayDensityForUserMethod.invoke(
                iWindowManager, 
                Display.DEFAULT_DISPLAY, 
                USER_ID
            );
        }
    }

    @SuppressLint("PrivateApi")
    public int getRealDensity() throws Exception {
        Class<?> iWindowManagerClass = Class.forName(WindowManagerConstants.IWindowManager.CLASS_NAME);
        Method getInitialDisplayDensityMethod = iWindowManagerClass.getMethod(
            WindowManagerConstants.IWindowManager.GET_INITIAL_DISPLAY_DENSITY,
            int.class
        );
        
        return (int) getInitialDisplayDensityMethod.invoke(
            iWindowManager, 
            Display.DEFAULT_DISPLAY
        );
    }
}
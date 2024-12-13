package node.ditzdev.pixelify;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Splash extends AppCompatActivity {
    private static final String PREFS_NAME = "PixelifyPrefs";
    private static final String FIRST_TIME_KEY = "first_time_launch";
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstTimeLaunch = prefs.getBoolean(FIRST_TIME_KEY, true);

        if (isFirstTimeLaunch) {
            showFirstTimeWarningDialog();
        } else {
            checkPermissionsAndNavigate();
        }
    }

    private void showFirstTimeWarningDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Warning: Potential System Modifications")
            .setMessage("This application may potentially disrupt the normal system functioning of your device. " +
                "It can modify screen resolution and device density, which might cause critical system issues. " +
                "\n\nUSE AT YOUR OWN RISK!\n\n" +
                "By proceeding, you acknowledge that any system damage is solely your responsibility.")
            .setCancelable(false)
            .setPositiveButton("I Understand and Agree", (dialog, which) -> {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                prefs.edit().putBoolean(FIRST_TIME_KEY, false).apply();
                checkPermissionsAndNavigate();
            })
            .setNegativeButton("Reject and Exit", (dialog, which) -> {
                finish();
            })
            .show();
    }

    private void checkPermissionsAndNavigate() {
        if (hasWriteSecureSettingsPermission()) {
            navigateToMainActivity();
        } else {
            navigateToGetStartup();
        }
    }

    private boolean hasWriteSecureSettingsPermission() {
        return ContextCompat.checkSelfPermission(
                this, 
                Manifest.permission.WRITE_SECURE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED;
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToGetStartup() {
        Intent intent = new Intent(this, GetStartup.class);
        startActivity(intent);
        finish();
    }
}
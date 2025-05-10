package iiec.ditzdev.pixelify.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import iiec.ditzdev.pixelify.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val permissionCheckInterval = 2000
    private var permissionHandler: Handler? = null
    private var permissionChecker: Runnable? = null
    private var isPermissionCheckerRunning = false
    private var logTextView: MaterialTextView? = null
    private var logScrollView: ScrollView? = null
    private val userId = "ed" + UUID.randomUUID().toString().substring(0, 6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        logTextView = binding?.logTextView
        logScrollView = binding?.logScrollView

        log("Application started")

        if (!hasWriteSecureSettingsPermission()) {
            log("Permission WRITE_SECURE_SETTINGS not granted")
            Toast.makeText(this, "Application requires permission WRITE_SECURE_SETTINGS", Toast.LENGTH_LONG).show()
            redirectToStartupActivity()
            return
        }

        log("WRITE_SECURE_SETTINGS permission granted")
        setupPermissionChecker()
    }

    private fun log(message: String) {
        val timestamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val logEntry = "[INFO] [$timestamp] $userId: $message\n"

        runOnUiThread {
            logTextView?.append(logEntry)
            // Auto-scroll to bottom
            logScrollView?.post {
                logScrollView?.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    private fun hasWriteSecureSettingsPermission(): Boolean {
        return try {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            e.printStackTrace()
            log("Exception checking permission: ${e.message}")
            false
        }
    }

    private fun setupPermissionChecker() {
        permissionHandler = Handler(Looper.getMainLooper())
        permissionChecker = Runnable {
            if (!hasWriteSecureSettingsPermission()) {
                log("Permission WRITE_SECURE_SETTINGS revoked")
                Toast.makeText(this@MainActivity, "Permission WRITE_SECURE_SETTINGS revoked", Toast.LENGTH_LONG).show()
                redirectToStartupActivity()
                return@Runnable
            }
            if (isPermissionCheckerRunning) {
                permissionHandler?.postDelayed(permissionChecker!!, permissionCheckInterval.toLong())
            }
        }
    }

    private fun redirectToStartupActivity() {
        log("Redirecting to StartupActivity")
        val intent = Intent(this, StartupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        log("Activity resumed")
        isPermissionCheckerRunning = true
        permissionHandler?.post(permissionChecker!!)
    }

    override fun onPause() {
        super.onPause()
        log("Activity paused")
        isPermissionCheckerRunning = false
        permissionHandler?.removeCallbacks(permissionChecker!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("Activity destroyed")
        if (permissionHandler != null && permissionChecker != null) {
            permissionHandler?.removeCallbacks(permissionChecker!!)
        }
        this.binding = null
    }

    private fun exportLogToFile() {
        try {
            val logText = binding?.logTextView?.text?.toString() ?: return

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "pixelify_log_$timestamp.txt"

            val file = File(getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { fos ->
                fos.write(logText.toByteArray())
            }

            Toast.makeText(this, "Log exported to $fileName", Toast.LENGTH_LONG).show()
            log("Log exported to file: $fileName")
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to export log: ${e.message}", Toast.LENGTH_LONG).show()
            log("Failed to export log: ${e.message}")
        }
    }
}
package iiec.ditzdev.pixelify.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import iiec.ditzdev.pixelify.Pixelify
import iiec.ditzdev.pixelify.databinding.ActivityMainBinding
import iiec.ditzdev.pixelify.R
import iiec.ditzdev.pixelify.models.Resolution
import iiec.ditzdev.pixelify.utils.NodeRESOUtils
import iiec.ditzdev.pixelify.utils.WM
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val permissionCheckInterval = 2000
    private var permissionHandler: Handler? = null
    private var permissionChecker: Runnable? = null
    private var isPermissionCheckerRunning = false
    private var logTextView: MaterialTextView? = null
    private var logScrollView: ScrollView? = null
    private val userId = "ed" + UUID.randomUUID().toString().substring(0, 6)
    private val PREFS_LOGGING: String = "prefs_logging"
    private lateinit var prefs: SharedPreferences
    private lateinit var wm: WM
    private lateinit var defaultReso: Resolution
    private lateinit var utils: NodeRESOUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(Pixelify.USER_PREFS, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        logTextView = binding?.logTextView
        logScrollView = binding?.logScrollView

        log("Application started")

        if (!hasWriteSecureSettingsPermission()) {
            log("Permission WRITE_SECURE_SETTINGS not granted")
            Toast.makeText(
                this,
                "Application requires permission WRITE_SECURE_SETTINGS",
                Toast.LENGTH_LONG
            ).show()
            redirectToStartupActivity()
            return
        }

        log("WRITE_SECURE_SETTINGS permission granted")
        setupPermissionChecker()

        binding?.exportLogButton?.setOnClickListener { v ->
            exportLogToFile()
        }

        // Toolbar
        binding?.toolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuSettings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> false
            }
        }
        try {
            wm = WM(contentResolver)
            utils = NodeRESOUtils(wm)
            defaultReso = utils.getRealResolution()
            log("Screen initialized with $defaultReso")
        } catch (e: Exception) {
            showSnackbar("Cannot initialize default Resolution.")
            log("Cannot initialize default Resolution:\n$e NOT_OK!")
        }
        try {
            binding?.dpiInput?.setText(wm.getRealDensity().toString())
            binding?.widthInput?.setText(defaultReso.width.toString())
            binding?.heightInput?.setText(defaultReso.height.toString())
        } catch (e: Exception) {
            log("Cannot get default of Resolution screen\n$e NOT_OK!")
        }

        binding?.btnReset?.setOnClickListener { v ->
            resetResolutionToDefault()
        }
        listener()
    }

    private fun applyReso(width: Int, height: Int, dpi: Int) {
        try {
            wm.setResolution(width, height)
            wm.setDisplayDensity(dpi)
            log("Resolution succesfully changed: $width|$height|$dpi")
        } catch (e: Exception) {
            log("Cannot apply Resolution changes...\n$e NOT_OK!")
        }
    }

    private fun showWarningDialog(width: Int, height: Int, dpi: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.warning))
            .setMessage(
                getString(
                    R.string.string_dialog_warning,
                    defaultReso.width.toString(),
                    defaultReso.height.toString(),
                    width.toString(),
                    height.toString(),
                    dpi.toString()
                )
            )
            .setPositiveButton(getString(R.string.continue_anyway)) { dialog, which ->
                applyReso(width, height, dpi)
                log("Successfuly change Resolution")
                showSnackbar("Success!")
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun resetResolutionToDefault() {
        try {
            wm.clearResolution()
            wm.clearDisplayDensity()
            binding?.widthInput?.setText(defaultReso.width.toString())
            binding?.heightInput?.setText(defaultReso.height.toString())
            binding?.dpiInput?.setText(wm.getRealDensity().toString())
            showSnackbar("Success to reset Resolution")
            log("Resolution reset: ${defaultReso.width}, ${defaultReso.height}, ${wm.getRealDensity()}")
        } catch (e: Exception) {
            showSnackbar("Failed to reset default Resolution")
            log("Could not reset Resolution to default\n$e NOT_OK!")
        }
    }

    private fun listener() {
        binding?.btnSubmit?.setOnClickListener { v ->
            prefs = getSharedPreferences(Pixelify.USER_PREFS, MODE_PRIVATE)
            val isPrefsOn = prefs.getBoolean(Pixelify.PREFS_SMART_ALERTS, true)
            val newWidth: Int = binding?.widthInput?.text.toString().toInt()
            val newHeight: Int = binding?.heightInput?.text.toString().toInt()
            val newDpi: Int = binding?.dpiInput?.text.toString().toInt()
            if (isDangeorous(newWidth, newHeight)) {
                if (isPrefsOn) {
                    showWarningDialog(newWidth, newHeight, newDpi)
                } else {
                    applyReso(newWidth, newHeight, newDpi)
                }
            } else {
                applyReso(newWidth, newHeight, newDpi)
            }
        }
    }

    private fun isDangeorous(width: Int, height: Int): Boolean {
        return abs(width - defaultReso.width) > defaultReso.width * 0.5
                || abs(height - defaultReso.height) > defaultReso.height * 0.5
    }

    private fun showSnackbar(teks: String) {
        Snackbar.make(findViewById(android.R.id.content), teks, Snackbar.LENGTH_SHORT).show()
    }

    private fun log(message: String) {
        val timestamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val logEntry = "[INFO] [$timestamp] $userId: $message\n"
        binding?.exportLogButton?.isEnabled = true
        val isLoggingEnabled = prefs.getBoolean(PREFS_LOGGING, true)
        if (!isLoggingEnabled) {
            return // Don't log if logging is disabled
        }

        runOnUiThread {
            logTextView?.append(logEntry)
            logScrollView?.post {
                logScrollView?.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }

    private fun updateLogVisibility() {
        val isLoggingEnabled = prefs.getBoolean(PREFS_LOGGING, true)
        if (!isLoggingEnabled) {
            logTextView?.text = ""
            binding?.exportLogButton?.isEnabled = false
        }
    }

    private fun hasWriteSecureSettingsPermission(): Boolean {
        return try {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_SECURE_SETTINGS
            ) == PackageManager.PERMISSION_GRANTED
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
                Toast.makeText(
                    this@MainActivity,
                    "Permission WRITE_SECURE_SETTINGS revoked",
                    Toast.LENGTH_LONG
                ).show()
                redirectToStartupActivity()
                return@Runnable
            }
            if (isPermissionCheckerRunning) {
                permissionHandler?.postDelayed(
                    permissionChecker!!,
                    permissionCheckInterval.toLong()
                )
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
        updateLogVisibility()
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
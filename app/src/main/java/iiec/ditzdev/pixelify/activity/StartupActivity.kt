package iiec.ditzdev.pixelify.activity

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import iiec.ditzdev.pixelify.databinding.ActivityStartupBinding
import  iiec.ditzdev.pixelify.R
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar

private var _binding: ActivityStartupBinding? = null
private val binding get() = _binding!!
class StartupActivity : AppCompatActivity() {

    private val permissionCheckHandler = Handler()
    private val PERMISSION_CHECK_INTERVAL = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startupFirstText.movementMethod = LinkMovementMethod.getInstance()
        val adbCommand = "pm grant " + packageName + " " + Manifest.permission.WRITE_SECURE_SETTINGS
        binding.commandText.append(adbCommand)

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuAdbHelper -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = "https://developer.android.com/tools/adb".toUri()
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("ADB Command", adbCommand)
        binding.commandText.setOnClickListener { v ->
            clipboardManager.setPrimaryClip(clipData)
            Snackbar.make(v, getString(R.string.startup_copy_toast), Snackbar.LENGTH_SHORT).show()
        }
        startPermissionCheckLoop()
    }

    private fun startPermissionCheckLoop() {
        val permissionChecker = object : Runnable {
            override fun run() {
                if (checkWriteSecureSettingsPermission()) {
                    navigatetoMain()
                } else {
                    permissionCheckHandler.postDelayed(this, PERMISSION_CHECK_INTERVAL)
                }
            }
        }

        permissionCheckHandler.post(permissionChecker)
    }

    private fun checkWriteSecureSettingsPermission(): Boolean {
        return try {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val testKey = "test_key_" + System.currentTimeMillis()
            Settings.Secure.putString(contentResolver, testKey, "test_value")
            Settings.Secure.putString(contentResolver, testKey, null)
            true
        } catch (e: SecurityException) {
            false
        }
    }

    private fun navigatetoMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package iiec.ditzdev.pixelify.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import iiec.ditzdev.pixelify.Pixelify
import iiec.ditzdev.pixelify.R
import iiec.ditzdev.pixelify.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private var binding: ActivitySettingsBinding? = null
    private val PREFS_SMART_ALERTS: String = "prefs_smart_alert"
    private val PREFS_LOGGING: String = "prefs_logging"
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences(Pixelify.USER_PREFS, MODE_PRIVATE)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbar?.setNavigationOnClickListener { v ->
            onBackPressedDispatcher.onBackPressed()
        }

        // Smart Alerts Setting
        val isPrefsAlertsOn: Boolean = prefs.getBoolean(PREFS_SMART_ALERTS, true /* Default true*/)
        binding?.btnEnableWarning?.setChecked(isPrefsAlertsOn)
        binding?.btnEnableWarning?.setOnClickListener { v ->
            val editor: SharedPreferences.Editor = prefs.edit()
            val currentChecked = binding?.btnEnableWarning?.isChecked() ?: false
            editor.putBoolean(PREFS_SMART_ALERTS, !currentChecked)
            editor.apply()
            // Update check state
            binding?.btnEnableWarning?.setChecked(!currentChecked)
        }

        // Logging Setting
        val isPrefsLogOn: Boolean = prefs.getBoolean(PREFS_LOGGING, true)
        binding?.btnEnableLog?.setChecked(isPrefsLogOn)
        binding?.btnEnableLog?.setOnClickListener { v ->
            val editor: SharedPreferences.Editor = prefs.edit()
            val currentChecked = binding?.btnEnableLog?.isChecked() ?: false
            editor.putBoolean(PREFS_LOGGING, !currentChecked)
            editor.apply()
            binding?.btnEnableLog?.setChecked(!currentChecked)
        }

        setupThemeModeSetting()
    }

    private fun setupThemeModeSetting() {
        updateThemeModeSubtitle()

        binding?.btnThemeMode?.setOnClickListener {
            showThemeModeDialog()
        }
    }

    private fun updateThemeModeSubtitle() {
        val currentThemeMode = prefs.getInt(Pixelify.THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val subtitleText = when (currentThemeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.light_mode)
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.dark_mode)
            else -> getString(R.string.follow_system)
        }
        binding?.btnThemeMode?.setSubtitle(subtitleText)
    }

    private fun showThemeModeDialog() {
        val currentThemeMode = prefs.getInt(Pixelify.THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val options = arrayOf(
            getString(R.string.light_mode),
            getString(R.string.dark_mode),
            getString(R.string.follow_system)
        )
        var selectedOption = when (currentThemeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.select_theme))
            .setSingleChoiceItems(options, selectedOption) { _, which ->
                selectedOption = which
            }
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                val themeMode = when (selectedOption) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                applyThemeMode(themeMode)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun applyThemeMode(themeMode: Int) {
        val editor = prefs.edit()
        editor.putInt(Pixelify.THEME_MODE, themeMode)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(themeMode)

        updateThemeModeSubtitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.binding = null
    }
}
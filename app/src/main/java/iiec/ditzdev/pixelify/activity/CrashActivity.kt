
package iiec.ditzdev.pixelify.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import iiec.ditzdev.pixelify.R
import org.json.JSONObject

class CrashActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CRASH_INFO = "extra_crash_info"
        const val EXTRA_CRASH_HTML_PATH = "extra_crash_html_path"
    }

    private var crashHtmlPath: String? = null
    private var crashInfo: String? = null
    private lateinit var crashHtmlDir: File

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        crashInfo = intent.getStringExtra(EXTRA_CRASH_INFO) ?: "Unknown error occurred"
        crashHtmlPath = intent.getStringExtra(EXTRA_CRASH_HTML_PATH)
        crashHtmlDir = File(getExternalFilesDir(null), "crash_reports")

        val tvErrorMessage = findViewById<TextView>(R.id.tvErrorMessage)
        tvErrorMessage.text = crashInfo

        val webView = findViewById<WebView>(R.id.webViewCrash)

        if (crashHtmlPath != null && File(crashHtmlPath!!).exists()) {
            webView.visibility = android.view.View.VISIBLE
            tvErrorMessage.visibility = android.view.View.GONE

            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.allowFileAccess = true
            webSettings.domStorageEnabled = true
            webSettings.allowContentAccess = true
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true

            webView.addJavascriptInterface(CrashJSInterface(this), "AndroidCrashInterface")

            webView.webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    val url = request.url.toString()

                    if (url.contains("crash_styles.css") || url.contains("crash_script.js")) {
                        try {
                            val fileName = if (url.contains("crash_styles.css")) "crash_styles.css" else "crash_script.js"
                            val file = File(crashHtmlDir, fileName)

                            val mimeType = if (fileName.endsWith(".css")) "text/css" else "application/javascript"

                            val inputStream = FileInputStream(file)

                            return WebResourceResponse(mimeType, "UTF-8", inputStream)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    return super.shouldInterceptRequest(view, request)
                }
            }

            val htmlFile = File(crashHtmlPath!!)

            val htmlContent = htmlFile.readText()

            val baseUrl = "file://${crashHtmlDir.absolutePath}/"

            webView.loadDataWithBaseURL(baseUrl, htmlContent, "text/html", "UTF-8", null)
        } else {
            webView.visibility = android.view.View.GONE
            tvErrorMessage.visibility = android.view.View.VISIBLE
        }

        findViewById<Button>(R.id.btnRestartApp).setOnClickListener {
            restartApp()
        }

        findViewById<Button>(R.id.btnShareLog).setOnClickListener {
            shareCrashLog()
        }
    }

    private fun restartApp() {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        finish()
    }

    private fun shareCrashLog() {
        try {
            val lastCrashFile = getSharedPreferences("crash_prefs", MODE_PRIVATE)
                .getString("last_crash_file", null)

            lastCrashFile?.let { filePath ->
                val crashFile = File(filePath)
                if (crashFile.exists()) {
                    val fileUri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        crashFile
                    )

                    Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(this, "Share Crash Log"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class CrashJSInterface(private val context: Context) {
        @JavascriptInterface
        fun getCrashData(): String {
            val crashLog = crashInfo ?: "Unknown error occurred"

            val timestamp = crashLog.substringAfter("Time: ").substringBefore("\n\n")

            val brand = extractInfo(crashLog, "Brand: ", "\n")
            val device = extractInfo(crashLog, "Device: ", "\n")
            val model = extractInfo(crashLog, "Model: ", "\n")
            val androidVersion = extractInfo(crashLog, "Android Version: ", "\n")
            val sdk = extractInfo(crashLog, "SDK: ", "\n")

            val stackTrace = if (crashLog.contains("========== START STACK TRACE =========")) {
                crashLog.substringAfter("========== START STACK TRACE =========\n\n")
                    .substringBefore("========== END OF STACK TRACE =========")
            } else {
                crashLog
            }

            val jsonData = JSONObject()
            jsonData.put("time", timestamp)

            val deviceJson = JSONObject()
            deviceJson.put("brand", brand)
            deviceJson.put("device", device)
            deviceJson.put("model", model)
            deviceJson.put("androidVersion", androidVersion)
            deviceJson.put("sdk", sdk)

            jsonData.put("device", deviceJson)
            jsonData.put("stackTrace", stackTrace)

            return jsonData.toString()
        }

        @JavascriptInterface
        fun restartApp() {
            this@CrashActivity.runOnUiThread {
                this@CrashActivity.restartApp()
            }
        }

        @JavascriptInterface
        fun shareCrashLog() {
            this@CrashActivity.runOnUiThread {
                this@CrashActivity.shareCrashLog()
            }
        }

        private fun extractInfo(text: String, startMarker: String, endMarker: String): String {
            return try {
                text.substringAfter(startMarker).substringBefore(endMarker)
            } catch (e: Exception) {
                "Unknown"
            }
        }
    }
}

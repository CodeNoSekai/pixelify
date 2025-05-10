package iiec.ditzdev.pixelify.handler

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import iiec.ditzdev.pixelify.activity.CrashActivity
import kotlin.jvm.Volatile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONObject

class CrashHandler private constructor(private val context: Context) : Thread.UncaughtExceptionHandler {

    // Default system uncaught exception handler
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        @Volatile
        private var instance: CrashHandler? = null

        /**
         * Initializes the CrashHandler and sets it as the default exception handler.
         *
         * @param application The Application context
         */
        fun initialize(application: Application) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = CrashHandler(application)
                        Thread.setDefaultUncaughtExceptionHandler(instance)
                    }
                }
            }
        }
    }

    /**
     * Handles uncaught exceptions by generating logs, saving crash data to files,
     * and launching CrashActivity to notify the user.
     *
     * @param thread The thread that has an uncaught exception
     * @param throwable The uncaught exception
     */
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val crashLog = generateCrashLog(throwable)
            val crashFile = saveCrashToFile(crashLog)
            val crashHtmlFile = saveCrashToHtmlFile(throwable)

            val sharedPrefs = context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE).edit()
            sharedPrefs.putString("last_crash_file", crashFile.absolutePath)
            sharedPrefs.putString("last_crash_html_file", crashHtmlFile.absolutePath)
            sharedPrefs.apply()

            object : Thread() {
                override fun run() {
                    Looper.prepare()

                    val intent = Intent(context, CrashActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra(CrashActivity.EXTRA_CRASH_INFO, crashLog)
                        putExtra(CrashActivity.EXTRA_CRASH_HTML_PATH, crashHtmlFile.absolutePath)
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    Looper.loop()
                }
            }.start()
            Thread.sleep(1000)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)

        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Generates a plain text crash log containing device and stack trace information.
     *
     * @param throwable The exception to log
     * @return A formatted crash log string
     */
    private fun generateCrashLog(throwable: Throwable): String {
        return buildString {
            append("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}\n\n")
            append("========== DEVICE INFORMATION =========\n")
            append("Brand: ${Build.BRAND}\n")
            append("Device: ${Build.DEVICE}\n")
            append("Model: ${Build.MODEL}\n")
            append("Android Version: ${Build.VERSION.RELEASE}\n")
            append("SDK: ${Build.VERSION.SDK_INT}\n")
            append("========== END OF DEVICE INFORMATION =========\n\n")
            append("========== START STACK TRACE =========\n\n")
            append(Log.getStackTraceString(throwable))
            append("========== END OF STACK TRACE =========\n\n")
        }
    }

    /**
     * Saves the crash log as a plain text file in external files directory.
     *
     * @param crashLog The crash log content
     * @return The File object pointing to the saved file
     */
    private fun saveCrashToFile(crashLog: String): File {
        val fileName = "crash_${System.currentTimeMillis()}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)
        FileOutputStream(file).use {
            it.write(crashLog.toByteArray())
        }
        return file
    }

    /**
     * Saves the crash information as a structured HTML file, including assets and JSON data.
     *
     * @param throwable The exception to represent in HTML
     * @return The File object pointing to the saved HTML file
     */
    private fun saveCrashToHtmlFile(throwable: Throwable): File {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val stackTrace = Log.getStackTraceString(throwable)

        val jsonData = JSONObject().apply {
            put("time", timestamp)
            put("device", JSONObject().apply {
                put("brand", Build.BRAND)
                put("device", Build.DEVICE)
                put("model", Build.MODEL)
                put("androidVersion", Build.VERSION.RELEASE)
                put("sdk", Build.VERSION.SDK_INT.toString())
            })
            put("stackTrace", stackTrace)
        }

        val crashReportDir = File(context.getExternalFilesDir(null), "crash_reports")
        if (!crashReportDir.exists()) {
            crashReportDir.mkdirs()
        }

        ensureAssetCopied("crash_template.html", crashReportDir)
        ensureAssetCopied("crash_styles.css", crashReportDir)
        ensureAssetCopied("crash_script.js", crashReportDir)

        val htmlFileName = "crash_${System.currentTimeMillis()}.html"
        val htmlFile = File(crashReportDir, htmlFileName)

        val templateFile = File(crashReportDir, "crash_template.html")
        val htmlTemplate = templateFile.readText()
        val injectedScript = """
            <script type="text/javascript">
                // Crash data JSON passed from Android
                window.crashData = ${jsonData.toString()};
            </script>
        """.trimIndent()

        val modifiedHtml = htmlTemplate.replace("</body>", "$injectedScript\n</body>")

        FileOutputStream(htmlFile).use {
            it.write(modifiedHtml.toByteArray())
        }

        return htmlFile
    }

    /**
     * Copies an asset file to the given destination directory if it does not already exist.
     *
     * @param assetName The name of the asset in assets folder
     * @param destinationDir The directory to copy the asset to
     */
    private fun ensureAssetCopied(assetName: String, destinationDir: File) {
        val destFile = File(destinationDir, assetName)

        if (!destFile.exists()) {
            try {
                context.assets.open(assetName).use { input ->
                    FileOutputStream(destFile).use { output ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (input.read(buffer).also { length = it } > 0) {
                            output.write(buffer, 0, length)
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("CrashHandler", "Failed to copy asset: $assetName", e)
            }
        }
    }
}

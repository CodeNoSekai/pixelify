package iiec.ditzdev.pixelify.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import iiec.ditzdev.pixelify.databinding.ActivitySplashBinding
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.seconds
import androidx.lifecycle.lifecycleScope

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            YoYo.with(Techniques.FadeIn)
                .duration(3000)
                .playOn(binding.textGreetings)
            YoYo.with(Techniques.FadeIn)
                .duration(3000)
                .playOn(binding.textSlogan)

            delay(4.seconds)

            val intent = if (hasPermission()) {
                Intent(this@Splash, MainActivity::class.java)
            } else {
                Intent(this@Splash, StartupActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }
}

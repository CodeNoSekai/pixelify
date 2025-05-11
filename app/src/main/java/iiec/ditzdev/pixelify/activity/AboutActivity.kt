package iiec.ditzdev.pixelify.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import iiec.ditzdev.pixelify.adapter.ContributorAdapter
import iiec.ditzdev.pixelify.models.Contributor
import iiec.ditzdev.pixelify.R
import android.widget.TextView

class AboutActivity : AppCompatActivity() {

    private val sourceCodeUrl = "https://github.com/DitzDev/pixelify"
    private val sponsorUrl = "https://opencollective.com/pixelify"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setupAppInfo()
        setupContributors()
        setupButtons()
    }

    private fun setupAppInfo() {
        val appVersion = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            "v${pInfo.versionName} (${pInfo.versionCode})"
        } catch (e: PackageManager.NameNotFoundException) {
            "v1.0.0"
        }

        findViewById<TextView>(R.id.tv_app_version).text = appVersion
    }

    private fun setupContributors() {
        val contributors = listOf(
            Contributor(
                name = "Aditya Pratama",
                role = "Lead Developer",
                avatarUrl = "https://github.com/DitzDev.png",
                githubUrl = "https://github.com/DitzDev"
            ),
            Contributor(
                name = "Salman",
                role = "Web Developer",
                avatarUrl = "https://github.com/salmanytofficial.png",
                githubUrl = "https://github.com/salmanytofficial"
            ),
            Contributor(
                name = "Alex",
                role = "Backend Developer",
                avatarUrl = "https://github.com/Paxsenix0.png",
                githubUrl = "https://github.com/Paxsenix0"
            ),
            Contributor(
                name = "Yukii >•<",
                role = "Full Stack Developer",
                avatarUrl = "https://github.com/YukiChanDev.png",
                githubUrl = "https://github.com/YukiChanDev"
            ),
            Contributor(
                name = "Sandri kun >//<",
                role = "Full Stack Developer",
                avatarUrl = "https://github.com/sandri-kun.png",
                githubUrl = "https://github.com/sandri-kun"
            ),
        )

        val recyclerView = findViewById<RecyclerView>(R.id.rv_contributors)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ContributorAdapter(contributors)
    }

    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btn_source_code).setOnClickListener {
            openUrl(sourceCodeUrl)
        }

        findViewById<MaterialButton>(R.id.btn_sponsor).setOnClickListener {
            openUrl(sponsorUrl)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

package Activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.collegetracker.R

class SocialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_social)

        val linkedinButton = findViewById<Button>(R.id.linkedinButton)

        linkedinButton.setOnClickListener {
            val linkedInUrl = "https://www.linkedin.com/company/surver-technologies/"

            // Intent for LinkedIn app
            val linkedInAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
            linkedInAppIntent.setPackage("com.linkedin.android")

            try {
                startActivity(linkedInAppIntent) // Try to open in LinkedIn app
            } catch (e: ActivityNotFoundException) {
                // Fallback to browser if LinkedIn app isn't installed
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrl))
                startActivity(browserIntent)
            }
        }
    }
}

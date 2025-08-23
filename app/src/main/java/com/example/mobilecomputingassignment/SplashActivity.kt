package com.example.mobilecomputingassignment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

  private val SPLASH_DELAY: Long = 2000 // 2 seconds

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // The splash screen background is already set via the theme
    // No need to set content view as the theme handles the background

    // Use a Handler to delay the transition to MainActivity
    Handler(Looper.getMainLooper())
            .postDelayed(
                    {
                      // Start MainActivity
                      val intent = Intent(this, MainActivity::class.java)
                      startActivity(intent)

                      // Close splash activity
                      finish()
                    },
                    SPLASH_DELAY
            )
  }
}

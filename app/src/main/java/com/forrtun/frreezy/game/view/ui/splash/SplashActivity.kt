package com.forrtun.frreezy.game.view.ui.splash

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
import com.forrtun.frreezy.game.view.ui.menu.MenuActivity
import com.forrtun.frreezy.game.view.ui.privacy.PrivacyActivity
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        runPrivacyActivity()
    }

    private fun runPrivacyActivity() {
        saveStatusPrivacy()
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)
        var startIntent: Intent

        animationProgressBar()

        Handler().postDelayed({
            if (!flagPrivacy) {
                startIntent = Intent(this@SplashActivity, PrivacyActivity::class.java)
                startActivity(startIntent)
                val editor = sharedPreferences.edit()
                editor.putBoolean("statusPrivacy", true)
                editor.apply()
            } else {
                startIntent = Intent(this@SplashActivity, MenuActivity::class.java)
                startActivity(startIntent)
            }
            finish()
        }, 3 * 1000.toLong())
    }

    private fun animationProgressBar() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels - 80

        val layoutParams = binding.progressBar.line.layoutParams
        val animation = ValueAnimator.ofInt(10, screenWidth).apply {
            duration = 3000
            addUpdateListener {
                val animatedValue = it.animatedValue as Int
                layoutParams.width = animatedValue
                binding.progressBar.line.layoutParams = layoutParams
            }
        }
        animation.start()
    }

    private fun saveStatusPrivacy() {
        sharedPreferences = getSharedPreferences("FortuneFreezyPref", MODE_PRIVATE)
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)
        val editor = sharedPreferences.edit()
        editor.putBoolean("statusPrivacy", flagPrivacy)
        editor.apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
package com.forrtun.frreezy.game.view.ui.splash

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen
import com.forrtun.frreezy.game.view.banner.LoadingBanner
import com.forrtun.frreezy.game.view.ui.menu.MenuActivity
import com.forrtun.frreezy.game.view.ui.privacy.PrivacyActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var loadingBanner: LoadingBanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        sharedPreferences = getSharedPreferences("FortuneFreezyPref", MODE_PRIVATE)
        loadingBanner = LoadingBanner(this, binding)
        runNextActivity()
    }

    private fun runNextActivity() {
        saveStatusPrivacy()
        animationProgressBar()
        Handler().postDelayed({
            if (!isInternetAvailable()) {
                runPrivacyActivity()
            } else {
                checkAndLoadBanner()
            }
        }, 2000)
    }

    private fun checkAndLoadBanner() {
        val hasUserData =
            sharedPreferences.contains("cookies") && sharedPreferences.contains("userAgent")

        if (hasUserData) {
            val cookies = sharedPreferences.getString("cookies", "")
            val userAgent = sharedPreferences.getString("userAgent", "")
            val actionUrl = sharedPreferences.getString("actionUrl", "")
            val sourceUrl = sharedPreferences.getString("sourceUrl", "")

            loadingBanner.loadImage(
                sourceUrl.toString(),
                actionUrl.toString(),
                cookies,
                userAgent
            )
        } else {
            loadingBanner.fetchInterstitialData()
        }
    }

    private fun runPrivacyActivity() {
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)
        if (!flagPrivacy) {
            startActivity(Intent(this@SplashActivity, PrivacyActivity::class.java))
            sharedPreferences.edit().putBoolean("statusPrivacy", true).apply()
        } else {
            startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
        }
        finish()
    }

    private fun animationProgressBar() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels - 80

        val layoutParams = binding.progressBar.line.layoutParams
        val animation = ValueAnimator.ofInt(10, screenWidth).apply {
            duration = 2000
            addUpdateListener {
                val animatedValue = it.animatedValue as Int
                layoutParams.width = animatedValue
                binding.progressBar.line.layoutParams = layoutParams
            }
        }
        animation.start()
    }

    private fun saveStatusPrivacy() {
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)
        sharedPreferences.edit().putBoolean("statusPrivacy", flagPrivacy).apply()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && (activeNetworkInfo.isConnected || activeNetworkInfo.isConnectedOrConnecting)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingBanner.cancel()
    }
}
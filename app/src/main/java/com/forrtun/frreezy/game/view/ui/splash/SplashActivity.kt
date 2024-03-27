package com.forrtun.frreezy.game.view.ui.splash

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen
import com.forrtun.frreezy.game.view.ui.menu.MenuActivity
import com.forrtun.frreezy.game.view.ui.privacy.PrivacyActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var linkJSON: String =
        "https://on.kabushinoko.com/interstitial" //TODO change on release
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cookies: String
    private lateinit var userAgent: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        runPrivacyActivity()
        sharedPreferences = getSharedPreferences("FortuneFreezyPref", MODE_PRIVATE)
    }

    private fun runPrivacyActivity() {
        saveStatusPrivacy()
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)

        animationProgressBar()

        Handler().postDelayed({
            val startTime = System.currentTimeMillis()
            if (!isInternetAvailable()) {
                if (!flagPrivacy) {
                    startActivity(Intent(this@SplashActivity, PrivacyActivity::class.java))
                    sharedPreferences.edit().putBoolean("statusPrivacy", true).apply()
                } else {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                }
                finish()
            } else {
                val hasUserData =
                    sharedPreferences.contains("cookies") && sharedPreferences.contains("userAgent")

                if (hasUserData) {
                    val cookies = sharedPreferences.getString("cookies", "")
                    val userAgent = sharedPreferences.getString("userAgent", "")
                    val actionUrl = sharedPreferences.getString("actionUrl", "")
                    val sourceUrl = sharedPreferences.getString("sourceUrl", "")

                    loadImage(sourceUrl.toString(), actionUrl.toString(), cookies, userAgent)
                } else {
                    fetchInterstitialData()
                }

                val endTime = System.currentTimeMillis()
                val elapsedTime = endTime - startTime

                if (elapsedTime > 5000 && !flagPrivacy) { // Якщо час завантаження перевищує 5 секунд
                    startActivity(Intent(this@SplashActivity, PrivacyActivity::class.java))
                    sharedPreferences.edit().putBoolean("statusPrivacy", true).apply()
                    finish()
                } else if (elapsedTime > 5000) {
                    startActivity(Intent(this@SplashActivity, MenuActivity::class.java))
                    finish()
                }
            }

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

    @SuppressLint("ObsoleteSdkInt")
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && (activeNetworkInfo.isConnected || activeNetworkInfo.isConnectedOrConnecting)
    }

    private fun fetchInterstitialData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(linkJSON)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    cookies = response.header("Set-Cookie") ?: ""
                    userAgent = response.header("User-Agent") ?: ""

                    val json = response.body?.string()
                    json?.let { parseJsonAndOpenLinks(it, cookies, userAgent) }
                }
            }
        })
    }

    private fun parseJsonAndOpenLinks(json: String, cookies: String?, userAgent: String?) {
        try {
            val jsonObject = JSONObject(json)
            val actionUrl = jsonObject.getString("action")
            val sourceUrl = jsonObject.getString("source")

            saveLinksAndHeadersToSharedPreferences(actionUrl, sourceUrl, cookies, userAgent)

            loadImage(sourceUrl, actionUrl, cookies, userAgent)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveLinksAndHeadersToSharedPreferences(
        actionUrl: String,
        sourceUrl: String,
        cookies: String?,
        userAgent: String?
    ) {
        val editor = sharedPreferences.edit()
        editor.putString("actionUrl", actionUrl)
        editor.putString("sourceUrl", sourceUrl)
        editor.putString("cookies", cookies)
        editor.putString("userAgent", userAgent)
        editor.apply()
    }

    private fun loadImage(
        imageUrl: String,
        actionUrl: String,
        cookies: String?,
        userAgent: String?
    ) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(imageUrl)
            .header("Cookie", cookies.toString())
            .header("User-Agent", userAgent.toString())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    // get banner and set
                    val inputStream = response.body?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    runOnUiThread {
                        binding.sourceBanner.visibility = View.VISIBLE
                        binding.sourceBanner.setImageBitmap(bitmap)
                        setClickListenerOnImage(actionUrl)
                    }
                }
            }
        })
    }

    private fun setClickListenerOnImage(actionUrl: String) {
        binding.sourceBanner.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(actionUrl))
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
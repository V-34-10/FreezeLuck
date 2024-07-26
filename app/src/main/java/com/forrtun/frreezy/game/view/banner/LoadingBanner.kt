package com.forrtun.frreezy.game.view.banner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.View
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LoadingBanner(
    private val activity: Activity,
    private val binding: ActivityMainBinding
) {
    private var linkJSON: String =
        "https://miracle-of-divas.com/welcome" //TODO change on release
    private lateinit var cookies: String
    private lateinit var userAgent: String
    private var sharedPreferences: SharedPreferences =
        activity.getSharedPreferences("FortuneFreezyPref", Context.MODE_PRIVATE)
    private var call: Call? = null
    private var isTimeUp = false
    private var latch = CountDownLatch(1)
    fun fetchInterstitialData() {
        latch = CountDownLatch(1)
        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url(linkJSON)
            .build()

        call = client.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) {
                    Log.i("LoadingBanner", "onFailure: Call was cancelled")
                } else {
                    e.printStackTrace()
                }
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                if (call.isCanceled()) {
                    Log.i("LoadingBanner", "onResponse: Call was cancelled")
                }
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    cookies = response.header("Set-Cookie") ?: ""
                    userAgent = response.header("User-Agent") ?: ""

                    val json = response.body?.string()
                    json?.let {
                        parseJsonAndOpenLinks(it, cookies, userAgent)
                    }
                }
                latch.countDown()
            }
        })
        if (!latch.await(10, TimeUnit.SECONDS)) {
            isTimeUp = true
            call?.cancel()
            runPrivacyActivity()
        }
    }

    private fun runPrivacyActivity() {
        val flagPrivacy = sharedPreferences.getBoolean("statusPrivacy", false)
        if (!flagPrivacy) {
            activity.runOnUiThread {
                activity.startActivity(Intent(activity, PrivacyActivity::class.java))
            }
            sharedPreferences.edit().putBoolean("statusPrivacy", true).apply()
        } else {
            activity.runOnUiThread {
                activity.startActivity(Intent(activity, MenuActivity::class.java))
            }
        }
        activity.finish()
    }

    fun cancel() {
        if (call?.isCanceled() == false) {
            call?.cancel()
        }
    }

    private fun parseJsonAndOpenLinks(json: String, cookies: String?, userAgent: String?) {
        try {
            val jsonObject = JSONObject(json)
            val actionUrl = jsonObject.getString("welcomeParam")
            val sourceUrl = jsonObject.getString("wolcomeLink")

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

    fun loadImage(
        imageUrl: String,
        actionUrl: String,
        cookies: String?,
        userAgent: String?
    ) {
        latch = CountDownLatch(1)
        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url(imageUrl)
            .header("Cookie", cookies.toString())
            .header("User-Agent", userAgent.toString())
            .build()

        call = client.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                latch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    // get banner and set
                    val inputStream = response.body?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    activity.runOnUiThread {
                        if (!isTimeUp) {
                            binding.sourceBanner.visibility = View.VISIBLE
                            binding.sourceBanner.setImageBitmap(bitmap)
                            setClickListenerOnImage(actionUrl)
                        }
                    }

                }
                latch.countDown()
            }
        })
        if (!latch.await(10, TimeUnit.SECONDS)) {
            isTimeUp = true
            call?.cancel()
            runPrivacyActivity()
        }
    }

    private fun setClickListenerOnImage(actionUrl: String) {
        binding.sourceBanner.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(actionUrl))
            activity.startActivity(intent)
        }
    }
}
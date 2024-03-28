package com.forrtun.frreezy.game.view.banner

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoadingBanner(
    private val activity: Activity,
    private val binding: ActivityMainBinding,
    private var sharedPreferences: SharedPreferences
) {
    private var linkJSON: String =
        "https://on.kabushinoko.com/interstitial" //TODO change on release
    private lateinit var cookies: String
    private lateinit var userAgent: String

    fun fetchInterstitialData() {
        val client = OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .build()
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

    fun loadImage(
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

                    activity.runOnUiThread {
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
            activity.startActivity(intent)
        }
    }
}
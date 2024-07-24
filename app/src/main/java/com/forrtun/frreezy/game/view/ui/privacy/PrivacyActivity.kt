package com.forrtun.frreezy.game.view.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityPrivacyBinding
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen
import com.forrtun.frreezy.game.view.ui.menu.MenuActivity

class PrivacyActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPrivacyBinding.inflate(layoutInflater) }
    private var linkPrivacyPolicy: String = "https://www.google.com" //TODO change on release
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        clickAccept()
    }

    private fun clickAccept() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.button_animation)
        val btnPrivacyListener = View.OnClickListener {
            binding.btnAccept.startAnimation(animationClick)
            runMenu()
        }
        binding.btnAccept.setOnClickListener(btnPrivacyListener)
        binding.textPrivacyLink.setOnClickListener {
            binding.textPrivacyLink.startAnimation(animationClick)
            runBrowsePrivacy()
        }
    }

    private fun runMenu() {
        startActivity(Intent(this@PrivacyActivity, MenuActivity::class.java))
        finish()
    }

    private fun runBrowsePrivacy() =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkPrivacyPolicy)))

    @Deprecated(
        "Deprecated in Java"
    )
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
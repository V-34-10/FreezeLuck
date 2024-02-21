package com.forrtun.frreezy.game.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityMenuBinding
import com.forrtun.frreezy.game.utils.FullScreen

class MenuActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMenuBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FullScreen.setFullScreen(this)
        runMenuGames()
    }

    private fun runMenuGames() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.button_animation)
        binding.btnPlay.setOnClickListener {
            binding.btnPlay.startAnimation(animationClick)
            startActivity(Intent(this@MenuActivity, MenuGamesActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
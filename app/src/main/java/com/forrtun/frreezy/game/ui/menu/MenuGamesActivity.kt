package com.forrtun.frreezy.game.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityMenuGamesBinding
import com.forrtun.frreezy.game.ui.scene.SceneActivity
import com.forrtun.frreezy.game.ui.settings.SettingsActivity
import com.forrtun.frreezy.game.utils.FullScreen
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen

class MenuGamesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuGamesBinding.inflate(layoutInflater) }
    private lateinit var listGames: List<String>
    private lateinit var listButtonName: List<ImageView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        buttonRunGame()
    }

    private fun buttonRunGame() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.button_animation)
        for ((i, buttonGame) in listButtonName.withIndex()) {
            buttonGame.setOnClickListener {
                it.startAnimation(animationClick)
                startActivity(
                    Intent(
                        this@MenuGamesActivity,
                        SceneActivity::class.java
                    ).putExtra("ButtonNameGame", listGames[i])
                )
                finish()
            }
        }
        binding.btnSettings.setOnClickListener {
            binding.btnSettings.startAnimation(animationClick)
            startActivity(Intent(this@MenuGamesActivity, SettingsActivity::class.java))
            finish()
        }
        binding.btnExit.setOnClickListener {
            binding.btnSettings.startAnimation(animationClick)
            finishAffinity()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@MenuGamesActivity, MenuActivity::class.java))
        finish()
    }
}
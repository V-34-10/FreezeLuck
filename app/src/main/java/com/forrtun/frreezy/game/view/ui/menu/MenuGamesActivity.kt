package com.forrtun.frreezy.game.view.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityMenuGamesBinding
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen
import com.forrtun.frreezy.game.view.ui.scene.SceneActivity
import com.forrtun.frreezy.game.view.ui.settings.SettingsActivity

class MenuGamesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuGamesBinding
    private lateinit var listGames: List<String>
    private lateinit var listButtonName: List<ImageView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listGames = listOf(
            getString(R.string.title_game_first),
            getString(R.string.title_game_second),
            getString(R.string.title_game_three),
            getString(R.string.title_game_four),
            getString(R.string.title_game_fife)
        )
        listButtonName = listOf(
            binding.btnGameFirst,
            binding.btnGameSecond,
            binding.btnGameThree,
            binding.btnGameFour,
            binding.btnGameFife
        )
        setFullScreen(this)
        buttonsRunGame()
    }

    private fun buttonsRunGame() {
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
            }
        }
        binding.btnSettings.setOnClickListener {
            binding.btnSettings.startAnimation(animationClick)
            startActivity(Intent(this@MenuGamesActivity, SettingsActivity::class.java))
        }
        binding.btnExit.setOnClickListener {
            binding.btnExit.startAnimation(animationClick)
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
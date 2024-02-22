package com.forrtun.frreezy.game.view.ui.menu

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityMenuGamesBinding
import com.forrtun.frreezy.game.view.ui.scene.SceneActivity
import com.forrtun.frreezy.game.view.ui.settings.SettingsActivity
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen

class MenuGamesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuGamesBinding.inflate(layoutInflater) }
    private lateinit var listGames: List<String>
    private lateinit var listButtonName: List<ImageView>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun runDialogLose() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.button_animation)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_lose)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
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
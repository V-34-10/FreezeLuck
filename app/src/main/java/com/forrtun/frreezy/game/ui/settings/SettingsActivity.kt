package com.forrtun.frreezy.game.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.databinding.ActivitySettingsBinding
import com.forrtun.frreezy.game.ui.menu.MenuGamesActivity
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SettingsActivity, MenuGamesActivity::class.java))
        finish()
    }
}
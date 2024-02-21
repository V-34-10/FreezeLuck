package com.forrtun.frreezy.game.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivityMainBinding
import com.forrtun.frreezy.game.databinding.ActivityMenuGamesBinding
import com.forrtun.frreezy.game.utils.FullScreen

class MenuGamesActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMenuGamesBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FullScreen.setFullScreen(this)
    }
}
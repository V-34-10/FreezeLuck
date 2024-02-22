package com.forrtun.frreezy.game.view.ui.scene

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivitySceneBinding
import com.forrtun.frreezy.game.view.ui.menu.MenuGamesActivity
import com.forrtun.frreezy.game.view.ui.scene.games.MinerFifeGameFragment
import com.forrtun.frreezy.game.view.ui.scene.games.MinerSecondGameFragment
import com.forrtun.frreezy.game.view.ui.scene.games.SlotsFirstGameFragment
import com.forrtun.frreezy.game.view.ui.scene.games.WheelFourGameFragment
import com.forrtun.frreezy.game.view.ui.scene.games.WheelThreeGameFragment
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen

class SceneActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySceneBinding.inflate(layoutInflater) }
    private val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        setFragmentGame()
    }

    @SuppressLint("CommitTransaction")
    private fun setFragmentGame() {
        val nameClickGame = intent.getStringExtra("ButtonNameGame")
        val fragmentGame: Fragment = when (nameClickGame) {
            getString(R.string.title_game_first) -> SlotsFirstGameFragment()
            getString(R.string.title_game_second) -> MinerSecondGameFragment()
            getString(R.string.title_game_three) -> WheelThreeGameFragment()
            getString(R.string.title_game_four) -> WheelFourGameFragment()
            getString(R.string.title_game_fife) -> MinerFifeGameFragment()
            else -> return
        }

        bundle.putString("ButtonNameGame", nameClickGame)
        fragmentGame.arguments = bundle

        addFragment(fragmentGame, R.id.container_fragment_game)
    }

    private fun addFragment(fragment: Fragment, containerId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            Intent(this@SceneActivity, MenuGamesActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }
}
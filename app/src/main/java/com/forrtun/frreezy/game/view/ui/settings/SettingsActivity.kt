package com.forrtun.frreezy.game.view.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.forrtun.frreezy.game.R
import com.forrtun.frreezy.game.databinding.ActivitySettingsBinding
import com.forrtun.frreezy.game.utils.FullScreen.setFullScreen
import com.forrtun.frreezy.game.view.ui.menu.MenuGamesActivity

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setFullScreen(this)
        buttonsControls()
        controlSoundBar()
    }

    private fun buttonsControls() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.button_animation)
        binding.btnRemove.setOnClickListener {
            it.startAnimation(animationClick)
            resetScore()
            Toast.makeText(applicationContext, "Total reset score", Toast.LENGTH_SHORT).show()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animationClick)
            startActivity(Intent(this@SettingsActivity, MenuGamesActivity::class.java))
            finish()
        }
    }

    private fun controlSoundBar() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        binding.seekBarSound.max = maxVolume
        val maxMusic = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        binding.seekBarMusic.max = maxMusic

        val currentMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentSoundVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING)

        binding.seekBarMusic.progress = currentMusicVolume
        binding.seekBarSound.progress = currentSoundVolume

        binding.seekBarSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChanged = false
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    progressChanged = true
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                progressChanged = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (progressChanged) {
                    val volume = seekBar.progress
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                }
            }
        })

        binding.seekBarMusic.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChanged = false
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    progressChanged = true
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                progressChanged = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (progressChanged) {
                    val volume = seekBar.progress
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, 0)
                }
            }
        })
    }

    private fun resetScore() {
        sharedPreferences = getSharedPreferences("FortuneFreezyPref", MODE_PRIVATE)
        sharedPreferences.edit().putString("total", getString(R.string.default_total_balance))
            .apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}
package fk.home.tictactoe.ui.game_over

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fk.home.tictactoe.R
import fk.home.tictactoe.ui.config.ConfigActivity
import fk.home.tictactoe.ui.utils.applyTheme
import kotlinx.android.synthetic.main.activity_game_over.*

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme(true)
        setContentView(R.layout.activity_game_over)
        setupUi()
    }

    private fun setupUi() {
        setupMessage()
        gameOverRematch.setOnClickListener {
            rematch()
        }

        gameOverBackToStart.setOnClickListener {
            backToStartScreen()
        }
    }

    private fun rematch() {
        val data = Intent()
        data.putExtra(RESULT_REMATCH, true)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun backToStartScreen() {
        startActivity(Intent(this, ConfigActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun setupMessage() {
        val winner = intent?.extras?.let {
            it.getString(PARAM_WINNER)
        }
        val message = if (winner.isNullOrEmpty()) {
            getString(R.string.game_over_draw)
        } else {
            getString(R.string.game_over_victory, winner)
        }

        gameOverTitle.text = message
    }

    companion object {
        val PARAM_WINNER = "PARAM_WINNER"
        val RESULT_REMATCH = "RESULT_REMATCH"
    }
}

package fk.home.tictactoe.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fk.home.tictactoe.R
import fk.home.tictactoe.ui.utils.applyTheme

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        setContentView(R.layout.activity_main)
        openGame()
    }

    private fun openGame() {
        supportFragmentManager?.let { supportFragmentManager ->
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, GameFragment())
                .commitAllowingStateLoss()
        }
    }
}

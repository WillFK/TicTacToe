package fk.home.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fk.home.tictactoe.ui.game.GameFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

package fk.home.tictactoe.ui.config

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fk.home.tictactoe.R
import fk.home.tictactoe.ui.utils.applyTheme

class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        setContentView(R.layout.activity_config)
        setupUi()
    }

    private fun setupUi() {
        supportFragmentManager?.let { fragmentManager ->
            fragmentManager.beginTransaction()
                .add(R.id.configContainer, ConfigFragment())
                .commitAllowingStateLoss()
        }
    }
}

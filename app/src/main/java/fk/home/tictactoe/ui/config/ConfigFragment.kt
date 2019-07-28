package fk.home.tictactoe.ui.config

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import fk.home.tictactoe.R
import fk.home.tictactoe.repository.PlayerRepository
import fk.home.tictactoe.storage.DataStorage
import fk.home.tictactoe.storage.LocalPreferencesStorage
import fk.home.tictactoe.ui.game.GameActivity
import fk.home.tictactoe.ui.utils.KEY_FLAG_DARK_MODE
import fk.home.tictactoe.ui.utils.applyTheme
import fk.home.tictactoe.ui.utils.getDarkTheme
import fk.home.tictactoe.ui.utils.getLightTheme
import kotlinx.android.synthetic.main.fragment_config.*

class ConfigFragment : Fragment(), ConfigView {

    private var presenter: ConfigPresenter? = null
    private var dataStorage: DataStorage? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { context ->
            dataStorage = LocalPreferencesStorage(context)
            presenter = ConfigPresenter(PlayerRepository(LocalPreferencesStorage(context))).apply {
                attach(this@ConfigFragment)
            }
            presenter?.init()
        }
        setupUi()
    }

    private fun setupUi() {
        configStartGame.setOnClickListener { presenter?.checkNavigateToGame() }
        configChangeName.setOnClickListener { openPlayerNameDialog() }
        val darkModeSelected = dataStorage?.fetchBoolean(KEY_FLAG_DARK_MODE) ?: false
        checkDarkTheme.setOnCheckedChangeListener { _, _->  }
        checkDarkTheme.isChecked = darkModeSelected
        checkDarkTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isShown) {
                dataStorage?.persistBoolean(KEY_FLAG_DARK_MODE, isChecked)
                buttonView.setOnCheckedChangeListener { _, _-> }
                activity?.let { activity ->
                    activity.finish()
                    activity.startActivity(activity.intent)
                }
            }
        }
    }

    override fun load(playerName: String) {
        configGreetings.text = getString(R.string.greetings, playerName)
    }

    override fun onPlayerNotSet() {
        openPlayerNameDialog()
    }

    private fun openPlayerNameDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Please, inform your name")

        val input = EditText(activity)

        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            presenter?.updateUserName(input.text.toString()) }

        builder.setCancelable(false)

        builder.create().apply {
            //window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND) BLUR is deprecated. Dim is the new Blur
            window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM )

            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            show()
            input.requestFocus()
        }
    }

    override fun onSettingPlayerNameError(error: Throwable) {
        activity?.let {
            Toast.makeText(activity, R.string.config_error_msg, Toast.LENGTH_LONG).show()
        }
    }

    override fun navigateToGame() {
        activity?.startActivity(Intent(activity, GameActivity::class.java))
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}

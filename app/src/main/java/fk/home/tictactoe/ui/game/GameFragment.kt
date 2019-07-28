package fk.home.tictactoe.ui.game

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import fk.home.tictactoe.R
import fk.home.tictactoe.game.GameValues
import kotlinx.android.synthetic.main.fragment_game.*
import androidx.annotation.ColorInt
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.ContextCompat
import fk.home.tictactoe.repository.PlayerRepository
import fk.home.tictactoe.storage.LocalPreferencesStorage
import fk.home.tictactoe.ui.game_over.GameOverActivity


class GameFragment : Fragment(), GameView {

    private val tilesId = listOf(
        R.id.tile1x1, R.id.tile1x2, R.id.tile1x3,
        R.id.tile2x1, R.id.tile2x2, R.id.tile2x3,
        R.id.tile3x1, R.id.tile3x2, R.id.tile3x3)

    private var presenter: GamePresenter? = null
    private var aiWaitingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        //Ideally, there should be some DI happening here
        presenter = GamePresenter(PlayerRepository(LocalPreferencesStorage(view.context))).apply { this.attach(this@GameFragment) }
        presenter?.startGame()
    }

    private fun setupUi() {
        setupGameListeners()
    }

    private fun setupGameListeners() {
        tilesId.forEachIndexed { index, i ->
            val tile = ticTacToeGame?.findViewById<View>(i)
            tile?.setOnClickListener { presenter?.readHumanMove(index) }
        }
        gameRestart.setOnClickListener { presenter?.startGame() }
    }

    override fun displayGameDrawn() {
        activity?.let { activity ->
            startActivityForResult(Intent(activity, GameOverActivity::class.java), GAME_OVER_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GAME_OVER_REQUEST_CODE -> processResultGameOver(data)
        }
    }

    private fun processResultGameOver(data: Intent?) {
        val rematch = data?.getBooleanExtra(GameOverActivity.RESULT_REMATCH, false) ?: false
        if (rematch) {
            presenter?.startGame()
        }
    }

    override fun displayVictory(player: String) {
        activity?.let { activity ->
            val intent = Intent(activity, GameOverActivity::class.java)
            val data = Bundle()
            data.putString(GameOverActivity.PARAM_WINNER, player)
            intent.putExtras(data)
            startActivityForResult(intent, GAME_OVER_REQUEST_CODE)
        }
    }

    /**
     * I'm redrawing the whole game every turn.
     * Ideally I should change only the tile which was modified
     */
    override fun updateGame(state: IntArray) {
        state.forEachIndexed { index, i ->
            ticTacToeGame?.findViewById<ImageView>(tilesId[index])?.let { tile ->
                when (i) {
                    GameValues.EMPTY.cellValue -> {
                        updateCell(tile, 0)
                    }
                    GameValues.X.cellValue -> {
                        updateCell(tile, R.drawable.x)
                    }
                    GameValues.O.cellValue -> {
                        updateCell(tile, R.drawable.o)
                    }
                }
            }
        }
    }

    private fun updateCell(tile: ImageView, symbolId: Int) {
        activity?.runOnUiThread {
            tile.setImageResource(symbolId)
        }
    }

    override fun dismissWaitingForAiMessage() {
        activity?.runOnUiThread {
            aiWaitingDialog?.dismiss()
        }
    }

    override fun displayGameBroken() {
        activity?.let {
            Toast.makeText(it, R.string.game_broken, Toast.LENGTH_LONG).show()
        }
    }

    override fun displayWaitingForAiMessage() {
        activity?.let { activity ->
            activity.runOnUiThread {
                /*
                    despite being deprecated (because it's not the way of waiting for an action)
                    it does what is was required for the challenge, so I thought it would be ok to use it here.
                 */
                ProgressDialog(activity).apply {
                    setTitle(R.string.game_ai_waiting_title)
                    setMessage(getString(R.string.game_ai_waiting_text))
                    isIndeterminate = true
                    aiWaitingDialog = this
                }.show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setupPlayerTags(nameX: String, nameO: String) {
        activity?.runOnUiThread {
            playerXTag.text = "$nameX: X"
            playerOTag.text = "$nameO: O"
        }
    }

    override fun updatePlayerTags(highlightX: Boolean, highlightO: Boolean) {
        activity?.runOnUiThread {
            context?.let { context ->
                val theme = context.theme

                val typedValue = TypedValue()
                theme.resolveAttribute(R.attr.playerTagColorRegular, typedValue, true)
                @ColorInt val colorRegular = typedValue.data

                theme.resolveAttribute(R.attr.playerTagColorHighlighted, typedValue, true)
                @ColorInt val colorHighlighted = typedValue.data

                if (highlightX) {
                    playerXTag.setTextColor(colorHighlighted)
                } else {
                    playerXTag.setTextColor(colorRegular)
                }

                if (highlightO) {
                    playerOTag.setTextColor(colorHighlighted)
                } else {
                    playerOTag.setTextColor(colorRegular)
                }
            }
        }
    }

    override fun updateTimer(formattedTime: String) {
        activity?.runOnUiThread {
            gameTimer?.text = formattedTime
        }
    }

    private companion object {
        const val GAME_OVER_REQUEST_CODE = 42
    }
}

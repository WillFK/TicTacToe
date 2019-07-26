package fk.home.tictactoe.ui.game

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat


class GameFragment : Fragment(), GameView {

    private val tilesId = listOf(
        R.id.tile1x1, R.id.tile1x2, R.id.tile1x3,
        R.id.tile2x1, R.id.tile2x2, R.id.tile2x3,
        R.id.tile3x1, R.id.tile3x2, R.id.tile3x3)

    private var presenter: GamePresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_game, container, false)
        presenter = GamePresenter().apply { this.attach(this@GameFragment) }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
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
    }
    override fun displayGameDrawn() {
        Log.d("FKZ", "game drawn")
    }

    override fun displayVictory(player: String) {
        Log.d("FKZ", "victory: $player")
    }

    /**
     * I'm redrawing the whole game every turn.
     * Ideally I should change only the tile which was modified
     */
    override fun updateGame(state: IntArray) {
        state.forEachIndexed { index, i ->
            ticTacToeGame?.findViewById<ImageView>(tilesId[index])?.let { tile ->
                when (i) {
                    GameValues.EMPTY.cellValue -> { tile.setImageResource(0) }
                    GameValues.X.cellValue -> { tile.setImageResource(R.drawable.symbol_x) }
                    GameValues.O.cellValue -> { tile.setImageResource(R.drawable.symbol_o) }
                }
            }
        }
    }

    override fun dismissWaitingForAiMessage() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayGameBroken() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayWaitingForAiMessage() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("SetTextI18n")
    override fun setupPlayerTags(nameX: String, nameO: String) {
        Log.d("FKZ", "setupPlayers names $nameX $nameO")
        playerXTag.text = "$nameX: X"
        playerOTag.text = "$nameO: O"
    }

    override fun updatePlayerTags(highlightX: Boolean, highlightO: Boolean) {
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

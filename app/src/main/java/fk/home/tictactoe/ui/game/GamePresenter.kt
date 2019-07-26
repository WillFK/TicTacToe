package fk.home.tictactoe.ui.game

import android.os.AsyncTask
import android.util.Log
import fk.home.tictactoe.game.MoveResult
import fk.home.tictactoe.game.TicTacToe
import fk.home.tictactoe.player.Player
import fk.home.tictactoe.player.RandomAI
import fk.home.tictactoe.ui.Presenter
import kotlin.random.Random


class GamePresenter : Presenter<GameView>() {

    private val game = TicTacToe()
    private lateinit var playerX : Player
    private lateinit var playerO : Player
    private lateinit var currentPlayer: Player
    private var turnControl = 0
    private var readHumanInput = false

    fun startGame() {
        AsyncTask.execute {
            assignPlayers()
            game.prepareNewGame().let {
                prepareForPlayerInput(it.state)
            }
        }
    }

    private fun prepareForPlayerInput(state: IntArray) {
        val turnRemainder = turnControl % 2
        view?.updatePlayerTags(turnRemainder == 0, turnRemainder == 1)
        currentPlayer.let { current ->
            when (current) {
                is Player.Human -> handleHumanTurn()
                is Player.AI -> handleAiTurn(current, state)
            }
        }
    }

    //Ends turn and prepare for next one (assumes the game is still going on)
    private fun endTurn(state: IntArray) {
        turnControl = ++turnControl % 2
        currentPlayer = if (turnControl == 0) playerX else playerO
        prepareForPlayerInput(state)
    }

    private fun handleHumanTurn() {
        readHumanInput = true
    }

    private fun handleAiTurn(player: Player.AI, state: IntArray) {
        readHumanInput = false
        view?.displayWaitingForAiMessage()
        val move = player.findMove(state)
        val result = game.makeMove(move)
        view?.dismissWaitingForAiMessage()

        /**
         * AI shouldn't be able to play an illegal move. If that happens,
         * halt the game otherwise the player may get stuck in a loop forever
         */
        if (result is MoveResult.Invalid) {
            view?.displayGameBroken()
        } else {
            processMoveResult(result)
        }
    }

    private fun assignPlayers() {
        //In the future I'd like to implement it in a way where both players could be Humans.
        if (Random.nextInt() % 2 == 0) {
            playerX = Player.Human("Human") // TODO
            playerO = RandomAI()
        } else {
            playerX = RandomAI()
            playerO = Player.Human("Human") //TODO
        }

        view?.setupPlayerTags(playerX.name, playerO.name)
        //Tic Tac Toe rules may vary, but usually X goes first
        currentPlayer = playerX
    }

    fun readHumanMove(position: Int) {
        if (readHumanInput) {
            AsyncTask.execute {
                val result = game.makeMove(position)
                processMoveResult(result)
            }
        }
    }

    private fun processMoveResult(result: MoveResult) {
        Log.d("FKZ", "$result")
        when (result) {
            is MoveResult.Invalid -> { Log.d("FKZ", "invalid move") }
            is MoveResult.GameUpdated -> {
                view?.updateGame(result.state)
                endTurn(result.state)
            }
            is MoveResult.GameOver.Draw -> {
                view?.updateGame(result.state)
                view?.displayGameDrawn()
            }
            is MoveResult.GameOver.Victory -> {
                view?.updateGame(result.state)
                println(result.state)
                view?.displayVictory(result.side.name)
            }
        }
    }
}
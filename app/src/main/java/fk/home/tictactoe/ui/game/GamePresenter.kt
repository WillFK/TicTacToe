package fk.home.tictactoe.ui.game

import android.os.AsyncTask
import android.util.Log
import fk.home.tictactoe.game.MoveResult
import fk.home.tictactoe.game.TicTacToe
import fk.home.tictactoe.player.Player
import fk.home.tictactoe.player.RandomAI
import fk.home.tictactoe.repository.IPlayerRepository
import fk.home.tictactoe.ui.Presenter
import java.util.*
import kotlin.random.Random


class GamePresenter(private val playerRepository: IPlayerRepository) : Presenter<GameView>() {

    private val game = TicTacToe()
    private lateinit var playerX : Player
    private lateinit var playerO : Player
    private lateinit var currentPlayer: Player
    private var turnControl = 0
    private var readHumanInput = false
    private var timerCounter = 0L
    private var timer: Timer? = null
    private var lastTimeReading = 0L
    private var timerIsRunning = false

    fun startGame() {
        AsyncTask.execute {
            turnControl = 0
            readHumanInput = false
            assignPlayers()
            preparetimer()
            game.prepareNewGame().let {
                view?.updateGame(it.state)
                prepareForPlayerInput(it.state)
            }
        }
    }

    private fun getFormattedTime():String {
        val minutes = timerCounter / (1000*60)
        val seconds = (timerCounter % (1000*60)) / 1000
        val milliseconds = timerCounter % 1000
        return "%02d:%02d:%03d".format(minutes, seconds, milliseconds)
    }

    private fun updateTimer() {
        val currentTime = System.currentTimeMillis()
        val diff = currentTime - lastTimeReading
        timerCounter += diff
        lastTimeReading = currentTime
        view?.updateTimer(getFormattedTime())
    }

    private fun preparetimer() {
        timerCounter = 0
        view?.updateTimer(getFormattedTime())
    }

    private fun stopTimer() {
        timer?.cancel()
        timerIsRunning = false
    }

    private fun resumeTimer() {
        if (!timerIsRunning) {
            lastTimeReading = System.currentTimeMillis()
            timerIsRunning = true
            timer = Timer().apply {
                this.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        updateTimer()
                    }
                }, 0, TIMER_RATE)
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
        resumeTimer()
    }

    private fun handleAiTurn(player: Player.AI, state: IntArray) {
        readHumanInput = false
        view?.displayWaitingForAiMessage()
        stopTimer()
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
            playerX = Player.Human(playerRepository.getPlayerName() ?: "Player")
            playerO = RandomAI()
        } else {
            playerX = RandomAI()
            playerO = Player.Human(playerRepository.getPlayerName() ?: "Player")
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
                stopTimer()
                view?.updateGame(result.state)
                view?.displayGameDrawn()
            }
            is MoveResult.GameOver.Victory -> {
                stopTimer()
                view?.updateGame(result.state)
                println(result.state)
                view?.displayVictory(currentPlayer.name)
            }
        }
    }

    private companion object {

        const val TIMER_RATE = 10L
    }
}
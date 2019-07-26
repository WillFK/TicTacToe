package fk.home.tictactoe.game

import android.util.Log
import java.lang.IllegalArgumentException

/**
 * Implement core rules of the game. Does not control WHO is playing it. this is something the UI layer should take care of
 */
class TicTacToe {

    private val state = IntArray(9) //pseudo-multidimensional
    private var turnControl = 0
    private var open = true

    fun prepareNewGame(): MoveResult.GameUpdated {
        (0 until 9).forEach { state[it] = 0 }
        turnControl = 0
        open = true
        return MoveResult.GameUpdated(state)
    }

    @Throws(IllegalArgumentException::class)
    fun makeMove(position: Int): MoveResult {
        if (position < 0 || position > 8) throw IllegalArgumentException("position should be a number between 0 and 8")
        return if (open) {
            if (state[position] == GameValues.EMPTY.cellValue) {
                state[position] = GameValues.values()[turnControl].cellValue
                val status = checkGameStatus()
                evaluateGameStatus(status)
            } else {
                MoveResult.Invalid
            }
        } else {
            MoveResult.Invalid
        }
    }

    private fun evaluateGameStatus(status: InternalGameStatus) : MoveResult {
        return when (status) {
            InternalGameStatus.CLOSED -> MoveResult.GameOver.Draw(state.clone())
            InternalGameStatus.OPEN -> {
                turnControl = ++turnControl % 2
                MoveResult.GameUpdated(state.clone())
            }
            InternalGameStatus.VICTORY -> {
                open = false
                MoveResult.GameOver.Victory(GameValues.values()[turnControl], state)
            }
        }
    }

    private fun checkGameStatus(): InternalGameStatus {
        var status: InternalGameStatus = InternalGameStatus.CLOSED
        gameCheckList().forEach { function ->
            val sum = function()
            if (checkVictory(sum)) {
                return InternalGameStatus.VICTORY
            }
            if (checkHasEmptyCells(sum))
                status = InternalGameStatus.OPEN
        }
        return status
    }

    //check if there is empty cells given the sum of the values of a segment
    private fun checkHasEmptyCells(sum: Int): Boolean {
        return sum < GameValues.lower().cellValue * 3
    }

    //check if any of the sides claimed this segment
    private fun checkVictory(sum: Int): Boolean {
        return sum == GameValues.X.cellValue * 3 || sum == GameValues.O.cellValue * 3
    }

    /**
     * List of functions which will sum the value of every row, column and diagonal.
     */
    private fun gameCheckList(): List<() -> Int> = listOf(
        checkRow(0), checkRow(1), checkRow(2), // rows
        checkColumn(0), checkColumn(1), checkColumn(2), //columns
        checkDiagonal(), checkAntiDiagonal() // diagonals
    )

    private fun checkRow(row: Int): () -> Int = {
        var sum = 0
        (row*3 until row*3+3).forEach {
            sum += state[it]
        }
        sum
    }


    private fun checkColumn(colunm: Int): () -> Int = {
        var sum = 0
        (0 until 3)
            .map { it * 3 + colunm}
            .forEach { sum += state[it] }
        sum
    }

    private fun checkDiagonal(): () -> Int = {
        var sum = 0
        (0 until 3)
            .map { it * 3 + it}
            .forEach { sum += state[it] }
        sum
    }

    private fun checkAntiDiagonal(): () -> Int = {
        var sum = 0
        (0 until 3)
            .map { it * 3 + (2-it)}
            .forEach { sum += state[it] }
        sum
    }
}

enum class GameValues(val cellValue: Int) {
    X(5),
    O(7),
    EMPTY(0);

    companion object {

        fun lower() = X
    }
}

enum class InternalGameStatus() {
    CLOSED,
    OPEN,
    VICTORY;
}

sealed class MoveResult {

    object Invalid : MoveResult()

    class GameUpdated(val state: IntArray) : MoveResult()

    sealed class GameOver : MoveResult() {

        class Draw(val state: IntArray) : GameOver()

        class Victory(val side: GameValues, val state: IntArray): GameOver()
    }
}

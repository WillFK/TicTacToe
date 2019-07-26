package fk.home.tictactoe.player

import android.util.Log
import fk.home.tictactoe.game.GameValues
import kotlin.random.Random

class RandomAI : Player.AI("Deeper Thought") {

    override fun findMove(state: IntArray): Int {
        Log.d("FKAI", "finding move")
        Thread.sleep(3000) //TODO
        while (true) {
            val randomPosition = Random.nextInt(0, 9)
            if (state[randomPosition] == GameValues.EMPTY.cellValue) {
                Log.d("FKAI", "found move")
                return randomPosition
            }
        }
    }
}
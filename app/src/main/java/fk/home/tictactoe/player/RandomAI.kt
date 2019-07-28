package fk.home.tictactoe.player

import android.util.Log
import fk.home.tictactoe.game.GameValues
import kotlin.math.abs
import kotlin.random.Random

class RandomAI : Player.AI("Deeper Thought") {

    override fun findMove(state: IntArray): Int {
        val timeout = 3000L + (abs(Random.nextInt()) % 5001L)
        Log.d("FKZ", "sleep: $timeout")
        Thread.sleep(timeout)
        while (true) {
            val randomPosition = Random.nextInt(0, 9)
            if (state[randomPosition] == GameValues.EMPTY.cellValue) {
                return randomPosition
            }
        }
    }
}
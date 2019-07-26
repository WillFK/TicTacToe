package fk.home.tictactoe.player

sealed class Player(val name: String) {

    class Human(name: String) : Player(name)

    abstract class AI(name: String) : Player(name) {

        abstract fun findMove(state: IntArray): Int
    }
}
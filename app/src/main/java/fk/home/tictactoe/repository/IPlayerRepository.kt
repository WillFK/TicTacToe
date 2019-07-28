package fk.home.tictactoe.repository

interface IPlayerRepository {

    fun savePlayerName(name: String)

    fun getPlayerName(): String?
}
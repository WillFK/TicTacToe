package fk.home.tictactoe.repository

import fk.home.tictactoe.storage.DataStorage

class PlayerRepository(private val dataStorage: DataStorage) : IPlayerRepository {

    override fun savePlayerName(name: String) {
        dataStorage.persistString(KEY_PLAYER_NAME, name)
    }

    override fun getPlayerName(): String? {
        return dataStorage.fetchString(KEY_PLAYER_NAME)
    }

    private companion object {
        const val KEY_PLAYER_NAME = "KEY_PLAYER_NAME"
    }
}
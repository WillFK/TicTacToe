package fk.home.tictactoe.ui.config

interface ConfigView {

    fun load(playerName: String)

    fun onPlayerNotSet()

    fun onSettingPlayerNameError(error: Throwable)

    fun navigateToGame()
}
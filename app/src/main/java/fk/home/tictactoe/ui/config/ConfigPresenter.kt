package fk.home.tictactoe.ui.config

import fk.home.tictactoe.repository.IPlayerRepository
import fk.home.tictactoe.ui.Presenter

class ConfigPresenter(
    private val playerRepository: IPlayerRepository) : Presenter<ConfigView>() {

    private var configReady = false

    fun init() {
        try {
            val playerName = playerRepository.getPlayerName()
            if (playerName.isNullOrEmpty()) {
                view?.onPlayerNotSet()
            } else {
                configReady = true
                view?.load(playerName)
            }
        } catch (ex: Throwable) {
            view?.onSettingPlayerNameError(ex)
        }
    }

    fun updateUserName(name: String) {
        try {
            playerRepository.savePlayerName(name)
            init()
        } catch (ex: Throwable) {
            view?.onSettingPlayerNameError(ex)
        }
    }

    fun checkNavigateToGame() {
        if (configReady) {
            view?.navigateToGame()
        }
    }
}
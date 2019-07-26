package fk.home.tictactoe.ui.game

interface GameView {

    fun setupPlayerTags(nameX: String, nameO: String)

    fun updatePlayerTags(highlightX: Boolean, highlightO: Boolean)

    fun updateGame(state: IntArray)

    fun displayGameDrawn()

    fun displayVictory(player: String)

    //Something went wrong and players should restart game
    fun displayGameBroken()

    fun displayWaitingForAiMessage()

    fun dismissWaitingForAiMessage()
}
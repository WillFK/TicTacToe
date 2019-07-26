package fk.home.tictactoe.ui

open class Presenter<T> {

    protected var view: T? = null

    fun attach(view: T) {
        this.view = view
    }

    fun dettach() {
        this.view = null
    }
}
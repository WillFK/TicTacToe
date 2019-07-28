package fk.home.tictactoe.storage

interface DataStorage {

    fun persistString(key: String, value: String)

    fun persistBoolean(key: String, value: Boolean)

    fun fetchString(key: String): String?

    fun fetchBoolean(key: String): Boolean?
}
package fk.home.tictactoe.storage

import android.content.Context
import android.util.Log

class LocalPreferencesStorage(context: Context) : DataStorage {

    private val preferences = context.getSharedPreferences("TicTacToeBasicStorage", Context.MODE_PRIVATE)

    override fun fetchString(key: String): String? {
        return preferences.getString(key, null) ?: null
    }

    override fun persistString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }


    override fun fetchBoolean(key: String): Boolean? {
        return preferences.getBoolean(key, false).apply {
            Log.d("FKZ", "fetch boolean: $key  -  $this")
        }
    }

    override fun persistBoolean(key: String, value: Boolean) {
        Log.d("FKZ", "persist boolean: $key  -  $value")
        preferences.edit().putBoolean(key, value).apply()
    }
}
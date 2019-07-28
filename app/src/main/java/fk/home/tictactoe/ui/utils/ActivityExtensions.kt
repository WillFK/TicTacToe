package fk.home.tictactoe.ui.utils

import android.app.Activity
import fk.home.tictactoe.R
import fk.home.tictactoe.storage.LocalPreferencesStorage

fun Activity.applyTheme(isDialog: Boolean = false) {
    val darkMode = LocalPreferencesStorage(this).fetchBoolean(KEY_FLAG_DARK_MODE) ?: false
    if (darkMode) {
        setTheme(getDarkTheme(isDialog))
    } else {
        setTheme(getLightTheme(isDialog))
    }
}

fun getLightTheme(isDialog: Boolean) = if (isDialog) R.style.Theme_App_Light_Dialog else R.style.Theme_App_Light

fun getDarkTheme(isDialog: Boolean) = if (isDialog) R.style.Theme_App_Dark_Dialog else R.style.Theme_App_Dark
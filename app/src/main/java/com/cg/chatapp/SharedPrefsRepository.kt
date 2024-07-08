package com.cg.chatapp

import android.content.Context

class SharedPrefsRepository(
    private val context: Context
) {
    fun getUser(): String =
        context.getSharedPreferences(USER, Context.MODE_PRIVATE)
            .getString(USER, "") ?: ""

    fun getUserAccess(): String =
        context.getSharedPreferences(USER_ACCESS, Context.MODE_PRIVATE)
            .getString(USER_ACCESS, "") ?: ""

    fun loginUser(username: String, userAccess: String) {
        context.getSharedPreferences(USER, Context.MODE_PRIVATE)
            .edit()
            .putString(USER, username)
            .apply()
        context.getSharedPreferences(USER_ACCESS, Context.MODE_PRIVATE)
            .edit()
            .putString(USER_ACCESS, userAccess)
            .apply()
    }

    fun logOutUser() {
        context.getSharedPreferences(USER, Context.MODE_PRIVATE)
            .edit()
            .remove(USER)
            .apply()
        context.getSharedPreferences(USER_ACCESS, Context.MODE_PRIVATE)
            .edit()
            .remove(USER_ACCESS)
            .apply()
    }

    companion object {
        private const val USER = "user"
        private const val USER_ACCESS = "userAccess"
    }
}
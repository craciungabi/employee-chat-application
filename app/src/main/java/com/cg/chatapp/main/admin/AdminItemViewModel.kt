package com.cg.chatapp.main.admin

class AdminItemViewModel(val title: String, val onClick: () -> Unit) {
    fun click() = onClick()
}
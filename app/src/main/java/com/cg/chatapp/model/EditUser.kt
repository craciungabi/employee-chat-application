package com.cg.chatapp.model

import kotlinx.coroutines.flow.MutableStateFlow

data class EditUser(
    val username: MutableStateFlow<String> = MutableStateFlow(""),
    val password: MutableStateFlow<String> = MutableStateFlow(""),
    val access: MutableStateFlow<String> = MutableStateFlow("")
)

fun EditUser.toUser() =
    User(
        username = this.username.value,
        password = this.password.value,
        access = this.access.value
    )
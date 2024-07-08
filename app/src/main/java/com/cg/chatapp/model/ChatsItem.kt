package com.cg.chatapp.model

data class ChatsItem(
    val username: String = "",
    val employeeName: String = "",
    val message: Message = Message()
)
package com.cg.chatapp.main.chats

import com.cg.chatapp.model.ChatsItem
import com.cg.chatapp.nicerFormat

class ChatsItemViewModel(
    private val details: ChatsItem
) {
    val employeeName: String
        get() = details.employeeName

    val username: String
        get() = details.username

    val message: String
        get() = details.message.message

    val time: String
        get() = "at " + details.message.time.nicerFormat()
}
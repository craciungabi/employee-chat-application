package com.cg.chatapp.model

import java.util.*

data class Message(
    val message: String = "",
    val time: Date = Date()
)
package com.cg.chatapp.chat

import com.cg.chatapp.model.Message
import com.cg.chatapp.nicerFormat
import java.text.SimpleDateFormat
import java.util.*

class MessageItemViewModel(
    private val details: Message,
    val isSender: Boolean
) {
    val message: String
        get() = details.message

    val time: String
        get() = details.time.nicerFormat()

    val timeAsDate: Date
        get() = details.time
}
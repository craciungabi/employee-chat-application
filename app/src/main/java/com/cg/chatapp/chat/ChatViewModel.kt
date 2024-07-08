package com.cg.chatapp.chat

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class ChatViewModel(
    private val context: Application,
    private val receiver: String
) : ViewModel() {
    val messages = MutableStateFlow<List<MessageItemViewModel>>(mutableListOf())
    val typedMessage = MutableStateFlow("")
    private var wasPopulated = false

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(context)
    }
    private val database = FirestoreDatabase()

    init {
        setupDatabaseLister()
        if (!wasPopulated) populate()
    }

    private fun populate() {
        val user = prefs.getUser()
        val allMessages = mutableListOf<MessageItemViewModel>()

        viewModelScope.launch {
            val sentMessages =
                database.getSentMessagesList(user, receiver).map {
                    MessageItemViewModel(it, true)
                }

            val receivedMessages =
                database.getSentMessagesList(receiver, user).map {
                    MessageItemViewModel(it, false)
                }

            allMessages.apply {
                addAll(sentMessages)
                if (user != receiver) addAll(receivedMessages)
                sortByDescending {
                    it.timeAsDate
                }
            }
            messages.value = allMessages
        }
    }

    private fun setupDatabaseLister() {
        database.messageListener(viewModelScope, receiver, prefs.getUser()) {
            if (it.isNotEmpty()) {
                it.map { message ->
                    MessageItemViewModel(message, true)
                }
                wasPopulated = true
                populate()
            }
        }
    }

    fun onSendClicked() {
        viewModelScope.launch {
            val message = getCurrentTypedMessage()
            if (message.message.isNotEmpty()) {
                database.addMessage(prefs.getUser(), receiver, message)
            }
            typedMessage.value = ""

            populate()
        }
    }

    private fun getCurrentTypedMessage() =
        Message(typedMessage.value, getTime())

    private fun getTime() = Calendar.getInstance().time
}
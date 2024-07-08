package com.cg.chatapp.main.chats

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.SharedPrefsRepository
import com.cg.chatapp.chat.MessageItemViewModel
import com.cg.chatapp.model.ChatsItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val context: Application
) : ViewModel() {
    private val database: FirestoreDatabase = FirestoreDatabase()

    val chats = MutableStateFlow<List<ChatsItemViewModel>>(emptyList())
    val navigateToChats = MutableSharedFlow<String>()

    private val prefs: SharedPrefsRepository by lazy {
        SharedPrefsRepository(context)
    }

    private var wasPopulated = false

    init {
        viewModelScope.launch {
            setupDatabaseLister()
            if (!wasPopulated) populate()
        }
    }

    fun onChatsItemClicked(chatsItemViewModel: ChatsItemViewModel) {
        viewModelScope.launch {
            navigateToChats.emit(chatsItemViewModel.username)
        }
    }

    private fun populate() {
        viewModelScope.launch {
            chats.value = database.getChatsList(prefs.getUser()).map {
                it.toChatsItemViewModel()
            }
        }
    }

    private suspend fun setupDatabaseLister() {
        var isRefreshNeeded = false
        database.chatsListener(viewModelScope, prefs.getUser()) {
            if (it.isNotEmpty()) {
                it.map { message ->
                    MessageItemViewModel(message, true)
                }
                isRefreshNeeded = true
            }
            if (isRefreshNeeded) {
                wasPopulated = true
                populate()
            }
        }
    }


    private fun ChatsItem.toChatsItemViewModel() =
        ChatsItemViewModel(this)
}
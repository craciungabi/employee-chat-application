package com.cg.chatapp.main.admin.add_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.model.EditUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddUserViewModel : ViewModel() {
    val user = MutableStateFlow(EditUser())
    val showToast = MutableSharedFlow<String>()
    val addUser = MutableSharedFlow<Boolean>()

    private val database: FirestoreDatabase = FirestoreDatabase()

    fun onAddUserClicked() {
        viewModelScope.launch {
            addUser.emit(true)
        }
    }

    fun addUser() {
        viewModelScope.launch {
            database.addUser(user.value.username.value, user.value)

            showToast.emit("User added.")
        }
    }
}
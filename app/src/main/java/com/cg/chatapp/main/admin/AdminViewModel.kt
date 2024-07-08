package com.cg.chatapp.main.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    val items = MutableStateFlow<List<AdminItemViewModel>>(mutableListOf())
    val navigateToScreen = MutableSharedFlow<AdminScreen>()

    init {
        populate()
    }

    fun populate() {
        items.value = mutableListOf(
            AdminItemViewModel(
                "Add User"
            ) {
                navigateToScreen(AdminScreen.ADD_USER)
            },
            AdminItemViewModel(
                "Add Employee"
            ) {
                navigateToScreen(AdminScreen.ADD_EMPLOYEE)
            },
            AdminItemViewModel(
                "Edit Employees"
            ) {
                navigateToScreen(AdminScreen.EDIT_EMPLOYEE)
            }
        )
    }

    private fun navigateToScreen(screen: AdminScreen) {
        viewModelScope.launch {
            navigateToScreen.emit(screen)
        }
    }
}
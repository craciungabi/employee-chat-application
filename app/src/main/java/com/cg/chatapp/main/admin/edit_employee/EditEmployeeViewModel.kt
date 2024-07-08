package com.cg.chatapp.main.admin.edit_employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.model.EditEmployee
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EditEmployeeViewModel : ViewModel() {
    val employee = MutableStateFlow(EditEmployee())
    val showToast = MutableSharedFlow<String>()
    val saveEditEmployee = MutableSharedFlow<Boolean>()

    private val database: FirestoreDatabase = FirestoreDatabase()

    fun populate(username: String) {
        viewModelScope.launch {
            employee.value = database.getEditEmployeeByUsername(username)
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            saveEditEmployee.emit(true)
        }
    }

    fun saveEditEmployee() {
        viewModelScope.launch {
            database.updateEmployee(employee.value.username.value, employee.value)

            showToast.emit("Employee updated.")
        }
    }
}
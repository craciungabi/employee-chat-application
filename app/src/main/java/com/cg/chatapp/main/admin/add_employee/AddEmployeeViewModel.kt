package com.cg.chatapp.main.admin.add_employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.model.EditEmployee
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AddEmployeeViewModel : ViewModel() {
    val employee = MutableStateFlow(EditEmployee())
    val showToast = MutableSharedFlow<String>()
    val addEmployee = MutableSharedFlow<Boolean>()


    private val database: FirestoreDatabase = FirestoreDatabase()

    fun onAddEmployeeClicked() {
        viewModelScope.launch {
            addEmployee.emit(true)
        }
    }

    fun addEmployee() {
        viewModelScope.launch {
            database.addEmployee(employee.value.username.value, employee.value)

            showToast.emit("Employee added.")
        }
    }
}
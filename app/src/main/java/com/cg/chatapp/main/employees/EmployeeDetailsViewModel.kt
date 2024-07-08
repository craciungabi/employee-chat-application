package com.cg.chatapp.main.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.model.Employee
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmployeeDetailsViewModel : ViewModel() {
    val employee = MutableStateFlow(Employee())

    private val database = FirestoreDatabase()

    fun populate(username: String) {
        viewModelScope.launch {
            employee.value = database.getEmployeeByUsername(username)
        }
    }
}
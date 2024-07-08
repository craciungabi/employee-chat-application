package com.cg.chatapp.main.admin.manage_employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.model.Employee
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.main.admin.edit_employee.EditEmployeeItemViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ManageEmployeesViewModel : ViewModel() {
    private val database: FirestoreDatabase = FirestoreDatabase()

    val employees = MutableStateFlow<List<EditEmployeeItemViewModel>>(emptyList())
    val navigateToDetails = MutableSharedFlow<String>()
    val showToast = MutableSharedFlow<String>()
    val deleteEmployee = MutableSharedFlow<String>()

    fun populate() {
        viewModelScope.launch {
            employees.value = database.getEmployees().toEmployeeItemViewModelList()
        }
    }

    fun onDeleteEmployeeClicked(username: String) {
        viewModelScope.launch {
            deleteEmployee.emit(username)
        }
    }

    fun deleteEmployee(username: String) {
        viewModelScope.launch {
            database.deleteEmployeeByUsername(username)

            showToast.emit("Employee deleted.")

            populate()
        }
    }

    fun onEmployeeItemClicked(employee: EditEmployeeItemViewModel) {
        viewModelScope.launch {
            navigateToDetails.emit(employee.username)
        }
    }

    private fun List<Employee>.toEmployeeItemViewModelList() =
        this.map {
            EditEmployeeItemViewModel(it)
        }
}
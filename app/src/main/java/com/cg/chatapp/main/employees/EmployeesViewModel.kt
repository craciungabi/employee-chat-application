package com.cg.chatapp.main.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cg.chatapp.FirestoreDatabase
import com.cg.chatapp.model.Employee
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class EmployeesViewModel : ViewModel() {
    private val database: FirestoreDatabase = FirestoreDatabase()

    val employees = MutableStateFlow<List<EmployeeDetailsItemViewModel>>(emptyList())
    val filteredEmployees = MutableStateFlow<List<EmployeeDetailsItemViewModel>>(emptyList())
    val filters = MutableSharedFlow<List<String>>()
    val navigateToDetails = MutableSharedFlow<String>()
    val navigateToChat = MutableSharedFlow<String>()

    fun populate() {
        viewModelScope.launch {
            employees.value = database.getEmployees().toEmployeeDetailsItemViewModelList()
        }
    }

    fun repopulate(filter: String) {
        filteredEmployees.value =
            when (filter) {
                REMOVE_FILTERS -> employees.value
                SORT_A_Z -> employees.value.sortedBy {
                    it.employeeName
                }
                SORT_Z_A -> employees.value.sortedByDescending {
                    it.employeeName
                }
                SORT_BY_START_DATE -> employees.value.sortedBy {
                    it.startDate
                }
                FULL_TIME_NORM -> employees.value.filter {
                    it.norm == FULL_TIME
                }
                PART_TIME_NORM -> employees.value.filter {
                    it.norm == PART_TIME
                }
                else -> employees.value.filter {
                    it.role == filter
                }
            }
    }

    fun onChatClicked(username: String) {
        viewModelScope.launch {
            navigateToChat.emit(username)
        }
    }

    fun onEmployeeItemClicked(employee: EmployeeDetailsItemViewModel) {
        viewModelScope.launch {
            navigateToDetails.emit(employee.username)
        }
    }

    fun getFilterItems() {
        viewModelScope.launch {
            val list: MutableList<String> = mutableListOf()
            list.add(REMOVE_FILTERS)
            list.add(SORT_A_Z)
            list.add(SORT_Z_A)
            list.add(SORT_BY_START_DATE)
            list.add(FULL_TIME_NORM)
            list.add(PART_TIME_NORM)
            list.addAll(database.getRoles().toList())
            filters.emit(list)
        }
    }

    private fun List<Employee>.toEmployeeDetailsItemViewModelList() =
        this.map {
            EmployeeDetailsItemViewModel(it)
        }

    companion object {
        private const val REMOVE_FILTERS = "Remove filters"
        private const val SORT_A_Z = "Sort A-Z by Name"
        private const val SORT_Z_A = "Sort Z-A by Name"
        private const val SORT_BY_START_DATE = "Sort A-Z by Start Date"
        private const val FULL_TIME_NORM = "Sort by Full Time Norm"
        private const val PART_TIME_NORM = "Sort by Part Time Norm"
        private const val FULL_TIME = "Full Time"
        private const val PART_TIME = "Part Time"
    }
}
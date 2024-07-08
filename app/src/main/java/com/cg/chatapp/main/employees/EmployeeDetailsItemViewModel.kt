package com.cg.chatapp.main.employees

import com.cg.chatapp.model.Employee

class EmployeeDetailsItemViewModel(
    private val employee: Employee
) {
    val username: String
        get() = employee.username

    val employeeName: String
        get() = employee.name

    val startDate: String
        get() = employee.startDate

    val role: String
        get() = employee.role

    val skill: String
        get() = employee.skill

    val norm: String
        get() = employee.norm
}
package com.cg.chatapp.model

import kotlinx.coroutines.flow.MutableStateFlow

data class Employee(
    val username: String = "",
    val name: String = "",
    val startDate: String = "",
    val role: String = "",
    val skill: String = "",
    val norm: String = ""
)

fun Employee.toEditEmployee() =
    EditEmployee(
        username = MutableStateFlow(this.username),
        name = MutableStateFlow(this.name),
        startDate = MutableStateFlow(this.startDate),
        role = MutableStateFlow(this.role),
        skill = MutableStateFlow(this.skill),
        norm = MutableStateFlow(this.norm)
    )
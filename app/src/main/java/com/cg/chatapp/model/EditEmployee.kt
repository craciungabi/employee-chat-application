package com.cg.chatapp.model

import kotlinx.coroutines.flow.MutableStateFlow

data class EditEmployee(
    val username: MutableStateFlow<String> = MutableStateFlow(""),
    val name: MutableStateFlow<String> = MutableStateFlow(""),
    val startDate: MutableStateFlow<String> = MutableStateFlow(""),
    val role: MutableStateFlow<String> = MutableStateFlow(""),
    val skill: MutableStateFlow<String> = MutableStateFlow(""),
    val norm: MutableStateFlow<String> = MutableStateFlow("")
)

fun EditEmployee.toEmployee() =
    Employee(
        username = this.username.value,
        name = this.name.value,
        startDate = this.startDate.value,
        role = this.role.value,
        skill = this.skill.value,
        norm = this.norm.value
    )
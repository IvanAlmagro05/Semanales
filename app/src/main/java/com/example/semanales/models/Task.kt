package com.example.semanales.models

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val date: Long, // Timestamp
    val isCompleted: Boolean = false,
    val hasTime: Boolean = false
)

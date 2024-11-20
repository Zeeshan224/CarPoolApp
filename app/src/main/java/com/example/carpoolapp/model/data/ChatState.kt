package com.example.carpoolapp.model.data

data class ChatState(
    val message: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
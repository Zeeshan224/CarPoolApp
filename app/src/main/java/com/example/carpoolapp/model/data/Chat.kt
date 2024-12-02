package com.example.carpoolapp.model.data

data class Chat(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val isUserInChat: Boolean = false,
    val error: String? = null
)

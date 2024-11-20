package com.example.carpoolapp.model.states

import com.example.carpoolapp.model.data.Chat

sealed class ChatState {
    object Loading : ChatState()
    data class Success(val message: List<Chat>)
}
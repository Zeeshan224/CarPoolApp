package com.example.carpoolapp.model.intents

sealed class ChatIntent {
    data class LoadMessages(val chatId: String) : ChatIntent()
    data class SendMessage(val chatId: String, val message: String) : ChatIntent()
}

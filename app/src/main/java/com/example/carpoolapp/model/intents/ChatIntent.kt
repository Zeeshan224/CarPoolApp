package com.example.carpoolapp.model.intents

sealed class ChatIntent {

    data object LoadMessages : ChatIntent()
    data class SendMessage(val message: String) : ChatIntent()
}
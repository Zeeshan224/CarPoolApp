package com.example.carpoolapp.model.intents

import com.example.carpoolapp.model.states.User

sealed class ChatRoomIntent {
    data object LoadChatRooms : ChatRoomIntent()
    data class OpenChat(val userId: String) : ChatRoomIntent()
}

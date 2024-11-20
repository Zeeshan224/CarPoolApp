package com.example.carpoolapp.model.states

import com.google.firebase.Timestamp

sealed class ChatRoomState {
    data object Empty : ChatRoomState()
    data class Success(val chatRooms: List<ChatRoom>) : ChatRoomState()
    data class Error(val message: String) : ChatRoomState()
}

data class User(
    val id: String,
    val name: String
)

data class LastMessage(
    val content : String,
    val timeStamp: Timestamp = Timestamp.now(),
    val isSent : Boolean
)

data class ChatRoom(
    val user: User,
    val lastMessage: LastMessage?
)
package com.example.carpoolapp.model.Intents

sealed class ChatIntent {
    data class CreateChatGroup(val groupName: String) : ChatIntent()
    data class AddMember(val groupId: String, val userId: String) : ChatIntent()
    data class RemoveMember(val groupName: String, val userId: String) : ChatIntent()
    data class LoadChatGroup(val groupId: String) : ChatIntent()
}

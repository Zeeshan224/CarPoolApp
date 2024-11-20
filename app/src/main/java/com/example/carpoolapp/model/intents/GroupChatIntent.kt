package com.example.carpoolapp.model.intents

sealed class GroupChatIntent {
    data class CreateGroupChatGroup(val groupName: String) : GroupChatIntent()
    data class AddMember(val groupId: String, val userId: String) : GroupChatIntent()
    data class RemoveMember(val groupName: String, val userId: String) : GroupChatIntent()
    data class LoadGroupChatGroup(val groupId: String) : GroupChatIntent()
}

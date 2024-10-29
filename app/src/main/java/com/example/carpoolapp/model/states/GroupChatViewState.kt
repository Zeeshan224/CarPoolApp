package com.example.carpoolapp.model.states

import com.example.carpoolapp.model.Group

sealed class GroupChatViewState {
    data object Idle : GroupChatViewState()
    data object Loading : GroupChatViewState()
    data class Success(val message : String) : GroupChatViewState()
    data class Error(val error : String) : GroupChatViewState()
    data class GroupChatGroupLoaded(val group : Group) : GroupChatViewState()
}

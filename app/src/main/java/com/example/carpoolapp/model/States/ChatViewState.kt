package com.example.carpoolapp.model.States

import com.example.carpoolapp.model.Group

sealed class ChatViewState {
    data object Idle : ChatViewState()
    data object Loading : ChatViewState()
    data class Success(val message : String) : ChatViewState()
    data class Error(val error : String) : ChatViewState()
    data class ChatGroupLoaded(val group : Group) : ChatViewState()
}

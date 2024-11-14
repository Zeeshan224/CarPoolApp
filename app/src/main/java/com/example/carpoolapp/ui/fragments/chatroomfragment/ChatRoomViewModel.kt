package com.example.carpoolapp.ui.fragments.chatroomfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.states.ChatRoom
import com.example.carpoolapp.model.states.ChatRoomState
import com.example.carpoolapp.model.states.LastMessage
import com.example.carpoolapp.model.states.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatRoomViewModel : ViewModel(){
    private val _state = MutableStateFlow<ChatRoomState>(ChatRoomState.Empty)
    val state : StateFlow<ChatRoomState> get() = _state
    private val users = FirebaseFirestore.getInstance()

    init {
        fetchChatRooms()
    }

    private fun fetchChatRooms(){
        viewModelScope.launch {
            try {
                val chatRooms = getChatRoomsFromFireStore()
                _state.value = if (chatRooms.isNotEmpty()){
                    ChatRoomState.Success(chatRooms)
                }
                else{
                    ChatRoomState.Empty
                }
            }
            catch (e: Exception){
                _state.value = ChatRoomState.Error("Failed to load chat rooms")
            }
        }
    }

    private suspend fun getChatRoomsFromFireStore() : List<ChatRoom>{
        val chatRooms = mutableListOf<ChatRoom>()
        val snapshot = users.collection("chatRooms").get().await()

        for (document in snapshot.documents){
            val userId = document.getString("userId") ?: continue
            val userName = document.getString("userName") ?: "Unknown"

            val lastMessageContent = document.getString("lastMessageContent") ?: ""
            val lastMessageTimestamp = document.getTimestamp("lastMessageTimeStamp") ?: Timestamp.now()
            val isSent = document.getBoolean("isSent") ?: false

            val user = User(id = userId, name = userName)
            val lastMessage = LastMessage(content = lastMessageContent, timeStamp = lastMessageTimestamp, isSent = isSent)

            chatRooms.add(ChatRoom(user = user, lastMessage = lastMessage))
        }
        return chatRooms
    }
}
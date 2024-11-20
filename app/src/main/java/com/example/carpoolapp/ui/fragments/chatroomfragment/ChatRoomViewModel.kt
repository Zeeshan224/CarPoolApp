package com.example.carpoolapp.ui.fragments.chatroomfragment

import android.util.Log
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

    private suspend fun getChatRoomsFromFireStore(): List<ChatRoom> {
        val chatRooms = mutableListOf<ChatRoom>()
        // Fetch all chat documents
        val chatSnapshot = users.collection("users").get().await()
        for (chatDocument in chatSnapshot.documents) {
            // Extract chat-related fields
            val chatId = chatDocument.id
            val lastMessageContent = chatDocument.getString("lastMessageContent") ?: ""
            val lastMessageTimestamp = chatDocument.getTimestamp("lastMessageTimeStamp") ?: Timestamp.now()

            // Fetch the user associated with this chat room
            val userSnapshot = users.collection("users").document(chatId).get().await()
            val userId = userSnapshot.getString("userId") ?: chatId
            val userName = userSnapshot.getString("userName") ?: "Unknown"

            // Construct the user object
            val user = User(id = userId, name = userName)

            // Check if there is a previous conversation
            val lastMessage = if (lastMessageContent.isNotEmpty()) {
                LastMessage(
                    content = lastMessageContent,
                    timeStamp = lastMessageTimestamp,
                    isSent = true // Replace with actual logic to determine send/receive
                )
            }
            else {
                null // No previous conversation
            }

            // Add ChatRoom to the list only if it has a valid lastMessage
            val chatRoom = ChatRoom(user = user, lastMessage = lastMessage)
            chatRooms.add(chatRoom)
        }

        Log.d("FirestoreData", "Fetched chatRooms: $chatRooms")

        return chatRooms
    }


}

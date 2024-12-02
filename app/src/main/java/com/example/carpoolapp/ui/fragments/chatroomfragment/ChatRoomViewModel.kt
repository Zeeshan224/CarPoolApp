package com.example.carpoolapp.ui.fragments.chatroomfragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.repository.ChatRepository
import com.example.carpoolapp.model.states.ChatRoom
import com.example.carpoolapp.model.states.ChatRoomState
import com.example.carpoolapp.model.states.LastMessage
import com.example.carpoolapp.model.states.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ChatRoomViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {
    private val _state = MutableStateFlow<ChatRoomState>(ChatRoomState.Empty)
    val state: StateFlow<ChatRoomState> get() = _state

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    init {
        fetchChatRooms()
    }

    private fun fetchChatRooms() {
        viewModelScope.launch {
            try {
                val chatRooms = getChatRoomsFromRealtimeDatabase()
                _state.value = if (chatRooms.isNotEmpty()) {
                    ChatRoomState.Success(chatRooms)
                } else {
                    ChatRoomState.Empty
                }
            } catch (e: Exception) {
                Log.e("ChatRoomViewModel", "Error fetching chat rooms", e)
                _state.value = ChatRoomState.Error("Failed to load chat rooms")
            }
        }
    }

    private suspend fun getChatRoomsFromRealtimeDatabase(): List<ChatRoom> {
        return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
            val chatRooms = mutableListOf<ChatRoom>()

            val loggedInUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            usersRef.get().addOnSuccessListener { snapshot ->
                Log.d("RealtimeDatabase", "Snapshot size: ${snapshot.childrenCount}")
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.child("userId").value as? String ?: ""

                    if (userId == loggedInUserId) continue

                    val userName = userSnapshot.child("userName").value as? String ?: "Unknown"
                    val lastMessageContent = userSnapshot.child("lastMessageContent").value as? String ?: ""

                    val lastMessageTimestampLong =
                        userSnapshot.child("lastMessageTimeStamp").value as? Long ?: System.currentTimeMillis()
                    val lastMessageTimestamp = Timestamp(lastMessageTimestampLong / 1000, 0)

                    val user = User(id = userId, name = userName)

                    val lastMessage = if (lastMessageContent.isNotEmpty()) {
                        LastMessage(
                            content = lastMessageContent,
                            timeStamp = lastMessageTimestamp,
                            isSent = true
                        )
                    } else {
                        null
                    }

                    val chatRoom = ChatRoom(user = user, lastMessage = lastMessage)
                    chatRooms.add(chatRoom)
                }

                Log.d("RealtimeDatabaseData", "Fetched chatRooms: $chatRooms")

                _state.value = if (chatRooms.isNotEmpty()){
                    ChatRoomState.Success(chatRooms)
                }
                else{
                    ChatRoomState.Empty
                }
//                continuation.resume(chatRooms) // Resume coroutine with the fetched chat rooms
            }.addOnFailureListener { exception ->
                Log.e("RealtimeDatabaseError", "Failed to fetch users", exception)
                continuation.resumeWith(Result.failure(exception)) // Resume coroutine with the error
            }
        }
    }

//    private suspend fun getChatRoomsFromRealtimeDatabase(): List<ChatRoom> {
//        val chatRooms = mutableListOf<ChatRoom>()
//
//        // Fetch all user data from Realtime Database
//        usersRef.get().addOnSuccessListener { snapshot ->
//            Log.d("RealtimeDatabase", "Snapshot size: ${snapshot.childrenCount}")
//            for (userSnapshot in snapshot.children) {
//                val userId = userSnapshot.child("userId").value as? String ?: ""
//                val userName = userSnapshot.child("userName").value as? String ?: "Unknown"
//                val lastMessageContent = userSnapshot.child("lastMessageContent").value as? String ?: ""
//
//                val lastMessageTimestampLong = userSnapshot.child("lastMessageTimeStamp").value as? Long ?: System.currentTimeMillis()
//                val lastMessageTimestamp = Timestamp(lastMessageTimestampLong/1000,0)
//                val user = User(id = userId, name = userName)
//
//                // Check if there is a previous conversation
//                val lastMessage = if (lastMessageContent.isNotEmpty()) {
//                    LastMessage(
//                        content = lastMessageContent,
//                        timeStamp = lastMessageTimestamp,
//                        isSent = true
//                    )
//                } else {
//                    null // No previous conversation
//                }
//
//                // Add ChatRoom to the list
//                val chatRoom = ChatRoom(user = user, lastMessage = lastMessage)
//                chatRooms.add(chatRoom)
//            }
//
//            Log.d("RealtimeDatabaseData", "Fetched chatRooms: $chatRooms")
//        }.addOnFailureListener {
//            Log.e("RealtimeDatabaseError", "Failed to fetch users", it)
//        }
//
//        return chatRooms
//    }

    fun getChatId(user1Id: String, user2Id: String): String{
        return chatRepository.generateChatId(user1Id,user2Id)
    }
}

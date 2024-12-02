package com.example.carpoolapp.ui.fragments.chatfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.data.Chat
import com.example.carpoolapp.model.data.Message
import com.example.carpoolapp.model.intents.ChatIntent
import com.example.carpoolapp.model.repository.ChatRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val chatRepository: ChatRepository) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val chatCollection = firestore.collection("chats")
    private val _state = MutableStateFlow(Chat())
    val state: StateFlow<Chat> = _state.asStateFlow()

    private val intentChannel = Channel<ChatIntent>(Channel.UNLIMITED)

    init {
        handleIntent()
    }

    fun startChat(userId: String, recipientId: String, onComplete: (String) -> Unit) {
        chatRepository.initiateChat(userId, recipientId, onComplete)
    }

    fun processIntent(intent: ChatIntent) {
        viewModelScope.launch {
            intentChannel.send(intent)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is ChatIntent.LoadMessages -> loadMessages(intent.chatId)
                    is ChatIntent.SendMessage -> sendMessages(intent.chatId,intent.message)
                }
            }
        }
    }

    private fun loadMessages(chatId: String) {
        _state.update { it.copy(isLoading = true) }

        chatCollection.document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    return@addSnapshotListener
                }

                snapshots?.let { querySnapshot ->
                    val messages = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }

                    _state.update {
                        it.copy(isLoading = false, messages = messages)
                    }
                }
            }
         }

//    private fun loadMessages(chatId: String) {
//        viewModelScope.launch {
//            _state.value = _state.value.copy(isLoading = true)
//
//            // Fetch chat data from the repository
//            val result = chatRepository.getChatMessages(chatId)
//
//            // Check if the user is part of the chat
//            val isUserInChat = result.users.contains(currentUserId()) // Assuming you have a way to get the current user's ID
//
//            _state.value = _state.value.copy(
//                isLoading = false,
//                messages = result.messages,
//                isUserInChat = isUserInChat,
//                error = if (!isUserInChat) "You are not part of this chat." else null
//            )
//        }
//    }


    private fun sendMessages(chatId: String, messageText: String){
        val newMessage = Message(
            id = generateId(),
            text = messageText,
            timestamp = com.google.firebase.Timestamp.now()
        )

        chatCollection.document(chatId).collection("messages")
            .add(newMessage)
            .addOnSuccessListener {

            }
            .addOnFailureListener{ error->
                _state.update { currentState ->
                    currentState.copy(error = error.message)
                }
            }
        }

//    private fun sendMessages(chatId: String, message: String) {
//        viewModelScope.launch {
//            // Check if the user is part of the chat
//            val chat = chatRepository.getChat(chatId)
//            if (chat.users.contains(currentUserId())) {
//                chatRepository.sendMessage(chatId, message)
//            } else {
//                _state.value = _state.value.copy(error = "You are not part of this chat.")
//            }
//        }
//    }

    private fun generateId(): String {
        return "msg_${System.currentTimeMillis()}"
    }

    // In ChatViewModel

    fun createChat(userId1: String, userId2: String) {
        val chatId = generateChatId(userId1, userId2)
        val chatData = mapOf(
            "users" to mapOf(
                userId1 to true,
                userId2 to true
            ),
            "lastUpdated" to FieldValue.serverTimestamp() // or the current timestamp
        )

        val chatDocRef = firestore.collection("chats").document(chatId)
        chatDocRef.set(chatData)
            .addOnSuccessListener {
                // Chat document created successfully
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun currentUserId(): String {
        // Fetch the current user's ID from your authentication system
        return "currentUserId" // Replace this with actual logic
    }

    private fun generateChatId(user1Id: String, user2Id: String): String {
        return chatRepository.generateChatId(user1Id, user2Id)
    }
}




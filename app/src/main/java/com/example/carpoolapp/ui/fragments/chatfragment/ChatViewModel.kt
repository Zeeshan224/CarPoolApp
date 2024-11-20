package com.example.carpoolapp.ui.fragments.chatfragment

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.data.Chat
import com.example.carpoolapp.model.data.Message
import com.example.carpoolapp.model.intents.ChatIntent
import com.example.carpoolapp.model.repository.ChatRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
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
                    is ChatIntent.LoadMessages -> loadMessages()
                    is ChatIntent.SendMessage -> sendMessages(intent.message)
                }
            }
        }
    }

    private fun loadMessages() {

        _state.update { it.copy(isLoading = true) }

        chatCollection
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    _state.update { it.copy(isLoading = false, error = error.message) }
                    return@addSnapshotListener
                }

//                snapshots?.let {
////                       doc.toObject(Message::class.java)!!
////
//////                        message.copy(
//////                            timestamp = message
//////                        )
////                    }
//                    _state.update { it.copy(isLoading = false, messages = it.messages) }
//                }

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

    private fun sendMessages(messageText: String) {
        val newMessage = Message(
            id = generateId(),
            text = messageText,
            timestamp = Timestamp.now()
        )

        chatCollection
            .add(newMessage)
            .addOnSuccessListener {

            }
            .addOnFailureListener { error ->
                _state.update { currentState ->
                    currentState.copy(error = error.message)
                }
            }
        }

    private fun generateId(): String {
        return "msg_${System.currentTimeMillis()}"
    }
}

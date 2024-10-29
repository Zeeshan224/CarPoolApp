package com.example.carpoolapp.ui.fragments.chatdashboardfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.Intents.ChatIntent
import com.example.carpoolapp.model.States.ChatViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import com.example.carpoolapp.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val userIntent = Channel<ChatIntent>(Channel.UNLIMITED)
    private val _viewState = MutableStateFlow<ChatViewState>(ChatViewState.Idle)
    val viewState: StateFlow<ChatViewState> = _viewState.asStateFlow()

    init {
        handleIntents()
    }
    private fun handleIntents() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is ChatIntent.CreateChatGroup -> createGroup(intent.groupName)
                    is ChatIntent.AddMember -> addMember(intent.groupId, intent.userId)
                    is ChatIntent.RemoveMember -> removeMember(intent.groupName, intent.userId)
                    is ChatIntent.LoadChatGroup -> loadGroup(intent.groupId)
                }
            }
        }
    }

    private suspend fun createGroup(groupName: String) {

        _viewState.value = ChatViewState.Loading
        val userId = auth.currentUser?.uid ?: return
        val groupId = db.collection("groups").document().id
        val group = Group(
            groupId = groupId,
            groupName = groupName,
            createdBy = userId,
            members = mapOf(userId to "admin")
        )

        try {
            db.collection("groups").document(groupId).set(group).await()
            _viewState.value = ChatViewState.Success("Group created successfully!")
        } catch (e: Exception) {
            _viewState.value = ChatViewState.Error("Failed to create group: ${e.message}")
        }
    }

    private suspend fun addMember(groupId: String, userId: String) {
        _viewState.value = ChatViewState.Loading
        try {
            db.collection("groups").document(groupId)
                .update("members.$userId", "member").await()
            _viewState.value = ChatViewState.Success("Member added successfully!")
        } catch (e: Exception) {
            _viewState.value = ChatViewState.Error("Failed to add member: ${e.message}")
        }
    }

    private suspend fun removeMember(groupId: String, userId: String) {
        _viewState.value = ChatViewState.Loading
        try {
            db.collection("groups").document(groupId)
                .update("members.$userId", null).await()
            _viewState.value = ChatViewState.Success("Member removed successfully!")
        } catch (e: Exception) {
            _viewState.value = ChatViewState.Error("Failed to remove member: ${e.message}")
        }
    }

    private suspend fun loadGroup(groupId: String) {
        _viewState.value = ChatViewState.Loading
        try {
            val snapshot = db.collection("groups").document(groupId).get().await()
            val group = snapshot.toObject(Group::class.java)
            group?.let {
                _viewState.value = ChatViewState.ChatGroupLoaded(it)
            } ?: run {
                _viewState.value = ChatViewState.Error("Group not found")
            }
        } catch (e: Exception) {
            _viewState.value = ChatViewState.Error("Failed to load group: ${e.message}")
        }
    }
}
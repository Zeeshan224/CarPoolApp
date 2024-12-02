package com.example.carpoolapp.ui.fragments.chatdashboardfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.intents.GroupChatIntent
import com.example.carpoolapp.model.states.GroupChatViewState
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

class DashboardViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val userIntent = Channel<GroupChatIntent>(Channel.UNLIMITED)

    private val _viewState = MutableStateFlow<GroupChatViewState>(GroupChatViewState.Idle)
    val viewState: StateFlow<GroupChatViewState> = _viewState.asStateFlow()

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is GroupChatIntent.CreateGroupChatGroup -> createGroup(intent.groupName)
                    is GroupChatIntent.AddMember -> addMember(intent.groupId, intent.userId)
                    is GroupChatIntent.RemoveMember -> removeMember(intent.groupName, intent.userId)
                    is GroupChatIntent.LoadGroupChatGroup -> loadGroup(intent.groupId)
                }
            }
        }
    }

    private suspend fun createGroup(groupName: String) {

        _viewState.value = GroupChatViewState.Loading
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
            _viewState.value = GroupChatViewState.Success("Group created successfully!")
        } catch (e: Exception) {
            _viewState.value = GroupChatViewState.Error("Failed to create group: ${e.message}")
        }
    }

    private suspend fun addMember(groupId: String, userId: String) {
        _viewState.value = GroupChatViewState.Loading
        try {
            db.collection("groups").document(groupId)
                .update("members.$userId", "member").await()
            _viewState.value = GroupChatViewState.Success("Member added successfully!")
        } catch (e: Exception) {
            _viewState.value = GroupChatViewState.Error("Failed to add member: ${e.message}")
        }
    }

    private suspend fun removeMember(groupId: String, userId: String) {
        _viewState.value = GroupChatViewState.Loading
        try {
            db.collection("groups").document(groupId)
                .update("members.$userId", null).await()
            _viewState.value = GroupChatViewState.Success("Member removed successfully!")
        } catch (e: Exception) {
            _viewState.value = GroupChatViewState.Error("Failed to remove member: ${e.message}")
        }
    }

    private suspend fun loadGroup(groupId: String) {
        _viewState.value = GroupChatViewState.Loading
        try {
            val snapshot = db.collection("groups").document(groupId).get().await()
            val group = snapshot.toObject(Group::class.java)
            group?.let {
                _viewState.value = GroupChatViewState.GroupChatGroupLoaded(it)
            } ?: run {
                _viewState.value = GroupChatViewState.Error("Group not found")
            }
        } catch (e: Exception) {
            _viewState.value = GroupChatViewState.Error("Failed to load group: ${e.message}")
        }
    }
}
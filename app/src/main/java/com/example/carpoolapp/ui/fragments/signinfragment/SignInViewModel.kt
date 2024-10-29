package com.example.carpoolapp.ui.fragments.signinfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.Intents.UserIntent
import com.example.carpoolapp.model.States.ViewState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val userIntent = Channel<UserIntent>(Channel.UNLIMITED)

    private val _state = MutableStateFlow<ViewState>(ViewState.Idle)
    val state: StateFlow<ViewState> = _state

    init {
        processIntents()
    }

    private fun processIntents() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is UserIntent.LogIn -> login(intent.email, intent.password)
                    is UserIntent.SignUp -> {} // Empty for SignInViewModel
                }
            }
        }
    }

    private suspend fun login(email: String, password: String) {
           return try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = ViewState.Success("Login Successful!")
            } catch (e: Exception) {
                _state.value = ViewState.Error("Login Failed: ${e.message}")
            }
        }
    }

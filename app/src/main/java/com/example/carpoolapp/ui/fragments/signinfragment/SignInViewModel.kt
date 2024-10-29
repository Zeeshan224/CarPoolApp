package com.example.carpoolapp.ui.fragments.signinfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.intents.AuthIntent
import com.example.carpoolapp.model.states.AuthViewState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow<AuthViewState>(AuthViewState.Idle)
    val state: StateFlow<AuthViewState> get() = _state
    val authIntent = Channel<AuthIntent>(Channel.UNLIMITED)

    private val auth = FirebaseAuth.getInstance()

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            authIntent.consumeAsFlow().collect{ intent ->
                when (intent) {
                    is AuthIntent.LogIn -> login(intent.email, intent.password)
                    is AuthIntent.ForgotPassword -> forgotPassword(intent.email) // Empty for SignInViewModel
                    is AuthIntent.SignUp -> {}
                }
            }
        }
    }

    private suspend fun login(email: String, password: String) {
           return try {
                auth.signInWithEmailAndPassword(email, password).await()
                _state.value = AuthViewState.Success("Login Successful!")
            } catch (e: Exception) {
                _state.value = AuthViewState.Error("Login Failed: ${e.message}")
            }
        }

    private fun forgotPassword(email: String) {
        _state.value = AuthViewState.IsLoading
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    _state.value = AuthViewState.ForgotPasswordSuccess("Password reset email  sent")
                }
                else{
                    _state.value = AuthViewState.Error(task.exception?.message ?: "Failed to send reset email")
                }
            }
    }
}

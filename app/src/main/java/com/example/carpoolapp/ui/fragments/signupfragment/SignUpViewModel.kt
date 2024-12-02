package com.example.carpoolapp.ui.fragments.signupfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carpoolapp.model.intents.AuthIntent
import com.example.carpoolapp.model.states.AuthViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Channel to accept user intents
    val authIntent = Channel<AuthIntent>(Channel.UNLIMITED)

    private val _state = MutableStateFlow<AuthViewState>(AuthViewState.Idle)
    val state: StateFlow<AuthViewState> = _state

    init {
        processIntents()
    }

    private fun processIntents() {
        viewModelScope.launch {
            authIntent.consumeAsFlow().collect { intent ->
                when (intent) {
                    is AuthIntent.SignUp -> signUp(intent.userName, intent.email, intent.password)
                    is AuthIntent.LogIn -> {}
                    is AuthIntent.ForgotPassword -> {}
                    is AuthIntent.GoogleSignIn -> {}
                    is AuthIntent.PhoneSignIn -> {}
                    is AuthIntent.VerifyOTP -> {}
                }
            }
        }
    }

    private suspend fun signUp(userName: String, email: String, password: String) {
        _state.value = AuthViewState.IsLoading
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val userId = auth.currentUser?.uid ?: throw Exception("User ID not found")

            saveUserToFireStore(userName, userId, email)

            sendVerificationEmail() // Send verification email after successful sign-up.
        } catch (e: Exception) {
            _state.value = AuthViewState.Error("Sign up Failed: ${e.message}")
        }
    }

    private suspend fun saveUserToFireStore(userName: String, userId: String, email: String) {
        val user = hashMapOf(
            "userName" to userName,
            "userId" to userId,
            "email" to email,
            "created_at" to System.currentTimeMillis()
        )

        try {
            firestore.collection("users").document(userId).set(user).await()
            _state.value =
                AuthViewState.Success("User registered successfully and saved to Firestore!")
        } catch (e: Exception) {
            _state.value = AuthViewState.Error("Failed to save User to Firestore: ${e.message}")
        }
    }

    private suspend fun sendVerificationEmail(){
        val currentUser = auth.currentUser
        try {
            currentUser?.sendEmailVerification()?.await()
            _state.value = AuthViewState.Success("Verification email sent. Please verify your email before signing in.")
        }
        catch (e: Exception){
            _state.value = AuthViewState.Error("Failed to send verification email: ${e.message}")
        }
    }
}

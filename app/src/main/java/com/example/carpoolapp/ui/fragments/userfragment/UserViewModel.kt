package com.example.carpoolapp.ui.fragments.userfragment

import androidx.lifecycle.ViewModel
import com.example.carpoolapp.model.data.UserProfile
import com.example.carpoolapp.model.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    fun saveUserProfile(firebaseUser: FirebaseUser) {
        userRepository.saveUserProfileIfNew(firebaseUser)
    }
}

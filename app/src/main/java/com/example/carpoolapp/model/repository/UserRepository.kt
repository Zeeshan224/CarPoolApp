package com.example.carpoolapp.model.repository

import com.example.carpoolapp.model.data.UserProfile
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository(private val firestore: FirebaseFirestore) {
    fun saveUserProfileIfNew(firebaseUser: FirebaseUser) {
        val userProfile = UserProfile(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName ?: "Anonymous",
        )
        firestore.collection("users").document(firebaseUser.uid)
            .set(userProfile)
            .addOnSuccessListener {
                // Success: User profile saved
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}


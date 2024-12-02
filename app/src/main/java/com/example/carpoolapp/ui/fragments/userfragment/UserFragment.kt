package com.example.carpoolapp.ui.fragments.userfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.carpoolapp.R
import com.example.carpoolapp.databinding.FragmentChatBinding
import com.example.carpoolapp.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null){
            userViewModel.saveUserProfile(firebaseUser)

            binding.apply {
                userName.text = firebaseUser.displayName
                userEmail.text = firebaseUser.email
                logoutButton.setOnClickListener{
                    logout()
                }
            }
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
        // Redirect to login screen or perform other necessary actions
    }
}

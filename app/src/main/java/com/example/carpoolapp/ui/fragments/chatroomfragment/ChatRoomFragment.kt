package com.example.carpoolapp.ui.fragments.chatroomfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpoolapp.databinding.FragmentChatRoomBinding
import com.example.carpoolapp.model.states.ChatRoom
import com.example.carpoolapp.model.states.ChatRoomState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatRoomViewModel by viewModels()
    private lateinit var chatRoomAdapter: ChatRoomAdapter
    private lateinit var currentUserId: String
    private lateinit var recipientId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        recipientId = arguments?.getString("recipientId") ?: ""

        setupRecyclerView()
        observeState()
    }

    private fun setupRecyclerView() {
        chatRoomAdapter = ChatRoomAdapter{
            val chatId = viewModel.getChatId(currentUserId, recipientId)
            val action = ChatRoomFragmentDirections.actionChatRoomFragmentToChatFragment(chatId)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatRoomAdapter
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when(state) {
                    is ChatRoomState.Success -> {
                        Log.d("ChatRoomFragment", "State Success: ${state.chatRooms}")
                        showChatRooms(state.chatRooms)
                    }
                    is ChatRoomState.Error -> showError(state.message)
                    ChatRoomState.Empty -> Log.d("ChatRoomFragment", "State Empty")
                }
            }
        }
    }

    private fun showChatRooms(chatRooms: List<ChatRoom>) {
        chatRoomAdapter.submitList(chatRooms)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



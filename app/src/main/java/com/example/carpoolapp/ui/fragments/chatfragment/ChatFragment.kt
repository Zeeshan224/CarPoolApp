package com.example.carpoolapp.ui.fragments.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpoolapp.databinding.FragmentChatBinding
import com.example.carpoolapp.model.data.Chat
import com.example.carpoolapp.model.intents.ChatIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val chatViewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ChatFragmentArgs by navArgs()
        val chatId = args.chatId

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    chatViewModel.state.collect { state ->
                        renderState(state)
                    }
                }
            }

        chatViewModel.processIntent(ChatIntent.LoadMessages(chatId))

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isNotEmpty()) {
                chatViewModel.processIntent(ChatIntent.SendMessage(chatId,message))
                binding.messageEditText.text.clear()
            }
        }
    }

    private fun renderState(state: Chat) {
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        state.error?.let {
        }
        chatAdapter.submitList(state.messages)
    }

//    private fun renderState(state: Chat) {
//        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
//        state.error?.let {
//            // Display an error message if needed
//            Toast.makeText(requireContext(), "Error: ${state.error}", Toast.LENGTH_SHORT).show()
//        }
//
//        // Ensure the messages are displayed if the user is part of the chat
//        if (state.isUserInChat) {
//            chatAdapter.submitList(state.messages)
//        } else {
//            // If the user is not part of the chat, show an error or exit
//            Toast.makeText(requireContext(), "You are not authorized to view this chat.", Toast.LENGTH_LONG).show()
//            if (!parentFragmentManager.isEx) {
//                lifecycleScope.launch {
//                    requireActivity().onBackPressedDispatcher.onBackPressed()
//                }
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

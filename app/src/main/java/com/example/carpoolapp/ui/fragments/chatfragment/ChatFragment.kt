package com.example.carpoolapp.ui.fragments.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpoolapp.databinding.FragmentChatBinding
import com.example.carpoolapp.model.data.Chat
import com.example.carpoolapp.model.intents.ChatIntent
import dagger.hilt.android.AndroidEntryPoint

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

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        lifecycleScope.launchWhenStarted {
            chatViewModel.state.collect { state ->
                renderState(state)
            }
        }

        chatViewModel.processIntent(ChatIntent.LoadMessages)
        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isNotEmpty()) {
                chatViewModel.processIntent(ChatIntent.SendMessage(message))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

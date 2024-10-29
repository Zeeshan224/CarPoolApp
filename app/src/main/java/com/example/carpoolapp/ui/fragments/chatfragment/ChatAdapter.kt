package com.example.carpoolapp.ui.fragments.chatfragment


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.carpoolapp.databinding.ItemMessageSentBinding
import com.example.carpoolapp.model.data.Message

class ChatAdapter : ListAdapter<Message, ChatAdapter.SentMessageViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentMessageViewHolder {
        val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SentMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SentMessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    class SentMessageViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageTextView.text = message.text
            binding.timestampTextView.text = message.timestamp.toString()
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
    }
}



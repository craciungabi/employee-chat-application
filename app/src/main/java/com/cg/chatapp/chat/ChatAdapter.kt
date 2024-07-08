package com.cg.chatapp.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cg.chatapp.databinding.MessageReceivedItemBinding
import com.cg.chatapp.databinding.MessageSentItemBinding

class ChatAdapter(
    private val chatLifeCycleOwner: LifecycleOwner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val messages = mutableListOf<MessageItemViewModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_MESSAGE_SENT -> {
                val binding = MessageSentItemBinding.inflate(layoutInflater, parent, false)
                MessageSentViewHolder(binding)
            }
            else -> {
                val binding = MessageReceivedItemBinding.inflate(layoutInflater, parent, false)
                MessageReceivedViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessageSentViewHolder -> holder.setMessage(messages[position])
            is MessageReceivedViewHolder -> holder.setMessage(messages[position])
        }

    }

    override fun getItemViewType(position: Int) =
        when (messages[position].isSender) {
            true -> TYPE_MESSAGE_SENT
            false -> TYPE_MESSAGE_RECEIVED
        }

    override fun getItemCount(): Int = messages.size

    fun submitList(messages: List<MessageItemViewModel>) {
        val diffCallback = MessagesDiffCallback(this.messages, messages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.messages.apply {
            clear()
            addAll(messages)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    private inner class MessageSentViewHolder(
        private val binding: MessageSentItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setMessage(messageItemViewModel: MessageItemViewModel) {
            binding.apply {
                viewModel = messageItemViewModel
                lifecycleOwner = chatLifeCycleOwner
            }
        }
    }

    private inner class MessageReceivedViewHolder(
        private val binding: MessageReceivedItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setMessage(messageItemViewModel: MessageItemViewModel) {
            binding.apply {
                viewModel = messageItemViewModel
                lifecycleOwner = chatLifeCycleOwner
            }
        }
    }

    private inner class MessagesDiffCallback(
        private val oldItems: List<MessageItemViewModel>,
        private val newItems: List<MessageItemViewModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].message == newItems[newItemPosition].message

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition].message == newItems[newItemPosition].message
    }

    companion object {
        private const val TYPE_MESSAGE_SENT = 0
        private const val TYPE_MESSAGE_RECEIVED = 1
    }
}
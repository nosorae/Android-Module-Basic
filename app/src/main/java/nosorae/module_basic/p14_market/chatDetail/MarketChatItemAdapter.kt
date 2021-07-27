package nosorae.module_basic.p14_market.chatDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nosorae.module_basic.databinding.ItemMarketChatBinding

class MarketChatItemAdapter: ListAdapter<MarketChatItem, MarketChatItemAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMarketChatBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MarketChatItem) {
            binding.itemMarketChatTextViewSender.text  = chat.senderId
            binding.itemMarketChatTextViewMessage.text = chat.message
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarketChatItemAdapter.ViewHolder
    = ViewHolder(ItemMarketChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: MarketChatItemAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    companion object {
        private val diffUtil = object :DiffUtil.ItemCallback<MarketChatItem>() {
            override fun areItemsTheSame(
                oldItem: MarketChatItem,
                newItem: MarketChatItem
            ): Boolean {
                // 키가 없네;;
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MarketChatItem,
                newItem: MarketChatItem
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}
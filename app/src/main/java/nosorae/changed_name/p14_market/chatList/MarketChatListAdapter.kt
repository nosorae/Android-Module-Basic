package nosorae.changed_name.p14_market.chatList


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nosorae.changed_name.databinding.ItemMarketChatRoomBinding

class MarketChatListAdapter(val onItemClicked: (MarketChatListItem) -> Unit) :
    ListAdapter<MarketChatListItem, MarketChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMarketChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: MarketChatListItem) {
            binding.itemMarketChatTextViewChatRoom.text = chat.itemTitle

            binding.root.setOnClickListener {
                onItemClicked(chat)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        ItemMarketChatRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<MarketChatListItem>() {
            // 리스트에 노출하고 있는 아이템과 새로운 아이템이 같은지 비교해줌
            // 새로운 게 들어올 때 호출됨
            // 보통 고유한 키값을 비교
            // 여기서는 아이템의 생성시간이 항상 같지 않을 것이라고 가정하고 createdAt 을 고유 키값으로 준다.
            override fun areItemsTheSame(
                oldItem: MarketChatListItem,
                newItem: MarketChatListItem
            ): Boolean {
                return oldItem.key == newItem.key
            }

            // 리스트에 노출하고 있는 아이템과 새로운 아이템이 == 인지 비교
            override fun areContentsTheSame(
                oldItem: MarketChatListItem,
                newItem: MarketChatListItem
            ): Boolean {
                return oldItem == newItem
            }
        }

    }
}
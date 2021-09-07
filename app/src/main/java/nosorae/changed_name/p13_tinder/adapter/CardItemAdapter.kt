package nosorae.changed_name.p13_tinder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nosorae.changed_name.R
import nosorae.changed_name.p13_tinder.model.CardItem

class CardItemAdapter: ListAdapter<CardItem, CardItemAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        fun bind(cardItem: CardItem) {
            view.findViewById<TextView>(R.id.item_tinder_card_text_view_name).text = cardItem.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_tinder_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CardItem>() {
            override fun areItemsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return  oldItem == newItem

            }

            override fun areContentsTheSame(oldItem: CardItem, newItem: CardItem): Boolean {
                return oldItem.userId == newItem.userId
            }
        }
    }
}
package nosorae.changed_name.p15_airbnb.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.changed_name.databinding.ItemAirHouseViewPagerBinding
import nosorae.changed_name.p15_airbnb.AirHouseModel

class AirHouseViewPagerAdapter(val itemClicked: (AirHouseModel)-> Unit): ListAdapter<AirHouseModel, AirHouseViewPagerAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemAirHouseViewPagerBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model : AirHouseModel) {
            binding.itemAirHouseViewPagerTextViewTitle.text = model.title
            binding.itemAirHouseViewPagerTextViewPrice.text = model.price
            Glide.with(binding.itemAirHouseViewPagerImageView.context)
                .load(model.imgUrl)
                .into(binding.itemAirHouseViewPagerImageView)

            binding.root.setOnClickListener {
                itemClicked(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ItemAirHouseViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<AirHouseModel>() {
            override fun areItemsTheSame(oldItem: AirHouseModel, newItem: AirHouseModel): Boolean
            = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AirHouseModel, newItem: AirHouseModel): Boolean
            = oldItem == newItem
        }
    }
}
package nosorae.module_basic.p17_music.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.module_basic.databinding.ItemMusicBinding
import nosorae.module_basic.p17_music.model.MusicModel

class MusicRecyclerAdapter(private val onItemClicked: (MusicModel) -> Unit): ListAdapter<MusicModel, MusicRecyclerAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemMusicBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MusicModel) {
            binding.itemMusicTextTitle.text = model.track
            binding.itemMusicTextSinger.text = model.artist
            Glide
                .with(binding.itemMusicCover.context)
                .load(model.coverUrl)
                .into(binding.itemMusicCover)

            if (model.isPlaying) {
                binding.root.setBackgroundColor(Color.GRAY)
            } else {
                binding.root.setBackgroundColor(Color.TRANSPARENT)
            }

            binding.root.setOnClickListener {
                onItemClicked(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<MusicModel>() {
            // 아이디값만 비교하는 것 Called by the DiffUtil to decide whether two object represent the same Item.
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean =
                oldItem.id == newItem.id

            // 객체안의 컨텐츠들을 비교하는 것
            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean =
                oldItem == newItem
        }
    }
}
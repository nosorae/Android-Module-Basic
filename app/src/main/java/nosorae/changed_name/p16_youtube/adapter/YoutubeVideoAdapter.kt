package nosorae.changed_name.p16_youtube.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.changed_name.databinding.ItemYoutubeVideoBinding
import nosorae.changed_name.p16_youtube.model.YoutubeVideoModel

class YoutubeVideoAdapter(val callback: (String, String) -> Unit): ListAdapter<YoutubeVideoModel, YoutubeVideoAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemYoutubeVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: YoutubeVideoModel) {
            binding.itemYoutubeVideoTextViewTitle.text = item.title
            binding.itemYoutubeVideoTextViewSubtitle.text = item.subtitle

            Glide.with(binding.itemYoutubeVideoImageViewThumbnail.context)
                .load(item.thumb)
                .into(binding.itemYoutubeVideoImageViewThumbnail)

            Glide.with(binding.itemYoutubeVideoImageViewLogo.context)
                .load(item.sources)
                .into(binding.itemYoutubeVideoImageViewLogo)

            binding.root.setOnClickListener {
                callback(item.sources.orEmpty(), item.title)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    = ViewHolder(ItemYoutubeVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    // = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.~ , parent, false)) 이렇게 하고 ViewHolder(view: View) 받으면 binding 없이 사용용


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<YoutubeVideoModel>() {
            override fun areItemsTheSame(
                oldItem: YoutubeVideoModel,
                newItem: YoutubeVideoModel
            ): Boolean = oldItem == newItem // 비디오마다 고유한 아이디가 없으니 그냥 객체 비교로 끝냄

            override fun areContentsTheSame(
                oldItem: YoutubeVideoModel,
                newItem: YoutubeVideoModel
            ): Boolean = oldItem == newItem
        }
    }
}
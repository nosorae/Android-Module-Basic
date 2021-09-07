package nosorae.changed_name.p22_unsplash.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import nosorae.changed_name.R
import nosorae.changed_name.databinding.ItemUnsplashPhotoBinding
import nosorae.changed_name.p22_unsplash.data.models.photo.PhotoResponse

class PhotoRecyclerAdapter : ListAdapter<PhotoResponse, PhotoRecyclerAdapter.ViewHolder>(diffUtil) {

    var onClickPhoto: (PhotoResponse) -> Unit = {}

    inner class ViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: PhotoResponse) {
            val dimensionRatio = data.height / data.width.toFloat()
            // 스크린의 가로사이즈
            val targetWidth = binding.root.resources.displayMetrics.widthPixels - (binding.root.paddingStart + binding.root.paddingEnd)
            val targetHeight = (dimensionRatio * targetWidth).toInt()


            binding.itemUnsplashPhotoContentContainer.layoutParams =
                binding.itemUnsplashPhotoContentContainer.layoutParams.apply {
                    height = targetHeight
                }

            Glide.with(binding.root)
                .load(data.urls?.regular)
                .thumbnail(
                    Glide.with(binding.root)
                        .load(data.urls?.thumb)
                        .transition(DrawableTransitionOptions.withCrossFade())
                )
                .override(targetWidth, targetHeight)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.itemUnsplashPhotoImageView)


            Glide.with(binding.root)
                .load(data.user?.profileImageUrls?.small)
                .placeholder(R.drawable.unsplash_profile_placeholder)
                .circleCrop()
                .into(binding.itemUnsplashPhotoProfileImageView)
            if (data.user?.name.isNullOrBlank()) {
                binding.itemUnsplashPhotoAuthorText.visibility = View.GONE
            } else {
                binding.itemUnsplashPhotoAuthorText.visibility = View.VISIBLE
                binding.itemUnsplashPhotoAuthorText.text = data.user?.name
            }
            if (data.description.isNullOrBlank()) {
                binding.itemUnsplashPhotoDescriptionText.visibility = View.GONE
            } else {
                binding.itemUnsplashPhotoDescriptionText.visibility = View.VISIBLE
                binding.itemUnsplashPhotoDescriptionText.text = data.description
            }






        }

        fun bindViews(data: PhotoResponse) {
            binding.root.setOnClickListener {
                onClickPhoto(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemUnsplashPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(currentList[position])
        holder.bindViews(currentList[position])
    }

    fun setOnItemClickListener(onClickPhoto: (PhotoResponse) -> Unit) {
        this.onClickPhoto = onClickPhoto
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<PhotoResponse>() {
            override fun areItemsTheSame(oldItem: PhotoResponse, newItem: PhotoResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PhotoResponse,
                newItem: PhotoResponse
            ): Boolean =
                oldItem == newItem
        }
    }
}
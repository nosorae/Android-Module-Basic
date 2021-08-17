package nosorae.module_basic.p20_git.adatper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.module_basic.databinding.ItemGitSearchBinding
import nosorae.module_basic.p20_git.data.entity.GithubRepository

class GitSearchRecyclerAdapter :
    ListAdapter<GithubRepository, GitSearchRecyclerAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val binding: ItemGitSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindViews(item: GithubRepository) {
            Glide.with(binding.itemGitSearchImageOwnerProfile.context)
                .load(item.owner.avatarUrl)
                .into(binding.itemGitSearchImageOwnerProfile)
            binding.itemGitSearchTextOwnerName.text =

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        = ViewHolder(ItemGitSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(currentList[position])
    }

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<GithubRepository>() {
            override fun areItemsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean = oldItem == newItem
        }
    }
}
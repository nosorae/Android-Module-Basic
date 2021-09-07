package nosorae.changed_name.p20_git.adatper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nosorae.changed_name.databinding.ItemGitSearchBinding
import nosorae.changed_name.p20_git.data.entity.GithubRepository
import nosorae.changed_name.p20_git.extensions.loadCenterInside

class GitSearchRecyclerAdapter(private val searchResultClickListener: (GithubRepository) -> Unit) :
    ListAdapter<GithubRepository, GitSearchRecyclerAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemGitSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: GithubRepository) = with(binding) {
            itemGitSearchImageOwnerProfile.loadCenterInside(
                item.owner.avatarUrl,
                24f
            ) // extensions 패키지의 ImageViewExtensions 참고
            itemGitSearchTextOwnerName.text = item.owner.login
            itemGitSearchTextTitle.text = item.fullName
            itemGitSearchTextSubtitle.text = item.name
            itemGitSearchTextStargazersCount.text = item.stargazersCount.toString()
            item.language?.let { lang ->
                itemGitSearchTextLanguage.isGone = false
                itemGitSearchTextLanguage.text = lang
            } ?: kotlin.run {
                itemGitSearchTextLanguage.isGone = true
                itemGitSearchTextLanguage.text = ""
            }

        }

        fun bindViews(item: GithubRepository) {
            binding.root.setOnClickListener {
                searchResultClickListener(item)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemGitSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(currentList[position])
        holder.bindViews(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<GithubRepository>() {
            override fun areItemsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean = oldItem.fullName == newItem.fullName

            override fun areContentsTheSame(
                oldItem: GithubRepository,
                newItem: GithubRepository
            ): Boolean = oldItem == newItem
        }
    }
}
package nosorae.module_basic.p14_market.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ItemMarketArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter: ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemMarketArticleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleModel) {
            val format = SimpleDateFormat("MM월 dd일")
            val date = Date(article.createdAt)
            binding.itemMarketArticleTextViewTitle.text = article.title
            binding.itemMarketArticleTextViewDate.text = format.format(date).toString()
            binding.itemMarketArticleTextViewPrice.text = article.price
            if(article.imageUrl.isNotEmpty()) {
                Glide
                    .with(binding.itemMarketArticleImageView)
                    .load(article.imageUrl)
                    .into(binding.itemMarketArticleImageView)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
       = ViewHolder(ItemMarketArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object: DiffUtil.ItemCallback<ArticleModel>() {
            // 리스트에 노출하고 있는 아이템과 새로운 아이템이 같은지 비교해줌
            // 새로운 게 들어올 때 호출됨
            // 보통 고유한 키값을 비교
            // 여기서는 아이템의 생성시간이 항상 같지 않을 것이라고 가정하고 createdAt 을 고유 키값으로 준다.
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.createdAt == newItem.createdAt
            }
            // 리스트에 노출하고 있는 아이템과 새로운 아이템이 == 인지 비교
            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }

    }
}
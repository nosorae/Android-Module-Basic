package nosorae.changed_name.p12_book.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nosorae.changed_name.databinding.ItemBookSearchHistoryBinding
import nosorae.changed_name.p12_book.model.BookSearchHistory

class BookSearchHistoryAdapter(
    val historyDeleteClickedListener: (String) -> Unit,
    val historyClickedListener: (String) -> Unit
) :
    ListAdapter<BookSearchHistory, BookSearchHistoryAdapter.BookSearchHistoryItemViewHolder>(
        diffUtil
    ) {

    // 미리 뷰들이 몇 개 만들어진 뷰가 있다 했는데 미리 만들어진 뷰가 뷰홀더
    inner class BookSearchHistoryItemViewHolder(private val binding: ItemBookSearchHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // 데이터 가져와서 뷰홀더에 집어넣는 함수
        fun bind(bookSearchHistory: BookSearchHistory) {
            binding.itemBookHistoryTextView.text = bookSearchHistory.keyword
            binding.itemBookHistoryImageButtonDelete.setOnClickListener {
                historyDeleteClickedListener(bookSearchHistory.keyword.orEmpty())
            }

            // 누르면 바로 검색결과 뷰를 띄울 수 있게 만들어보기기
           binding.root.setOnClickListener {
               historyClickedListener(bookSearchHistory.keyword.orEmpty())
            }
        }


    }


    // view 에도 context 가 있어서 ViewGroup 의 context 를 가져왔다.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookSearchHistoryItemViewHolder {
        return BookSearchHistoryItemViewHolder(
            ItemBookSearchHistoryBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    // 뷰홀더가 바인드 되기 위해 호출
    override fun onBindViewHolder(holder: BookSearchHistoryItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<BookSearchHistory>() {
            // 아래 두 함수를 보고 새로운 값을 새로 업데이트할 건지 지울 건지 새로 삽입할건지 판단해주는 게 diffUtil
            override fun areItemsTheSame(
                oldItem: BookSearchHistory,
                newItem: BookSearchHistory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BookSearchHistory,
                newItem: BookSearchHistory
            ): Boolean {
                return oldItem.keyword == newItem.keyword
            }
        }
    }


}
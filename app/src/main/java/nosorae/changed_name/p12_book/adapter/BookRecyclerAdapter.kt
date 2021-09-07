package nosorae.changed_name.p12_book.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nosorae.changed_name.databinding.ItemBookBinding
import nosorae.changed_name.p12_book.model.Book

class BookRecyclerAdapter(val itemClickedListener: (Book)-> Unit): ListAdapter<Book, BookRecyclerAdapter.BookItemViewHolder>(diffUtil) {

    // 미리 뷰들이 몇 개 만들어진 뷰가 있다 했는데 미리 만들어진 뷰가 뷰홀더
    inner class BookItemViewHolder(private val binding: ItemBookBinding): RecyclerView.ViewHolder(binding.root) {
        // 데이터 가져와서 뷰홀더에 집어넣는 함수
        fun bind(bookModel: Book) {

            binding.root.setOnClickListener {
                itemClickedListener(bookModel)
            }

            binding.itemBookTextViewTitle.text = bookModel.title
            binding.itemBookTextViewContent.text = bookModel.description
            // View 에도 context 가 있음을 기억하라!
            Glide
                .with(binding.root.context)
                .load(bookModel.coverSmallUrl) // 인터파크가 제공하는 url 이 http 라서 android:usesCleartextTraffic="true" 추가함
                .into(binding.itemBookImageViewCover)
        }


    }


    // view 에도 context 가 있어서 ViewGroup 의 context 를 가져왔다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        return BookItemViewHolder(ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    // 아래 코드는 p10_quote 에서 LayoutInflater 로 View 를 생성한 코드이다.
    //QuoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false))

    // 뷰홀더가 바인드 되기 위해 호출
    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Book>() {
            // 아래 두 함수를 보고 새로운 값을 새로 업데이트할 건지 지울 건지 새로 삽입할건지 판단해주는 게 diffUtil
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


}
package nosorae.module_basic.p10_wise.pager_adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nosorae.module_basic.R
import nosorae.module_basic.p10_wise.Quote

class QuotesPagerAdapter(
    private val quotes: List<Quote>,
    private val isNameRevealed: Boolean
): RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() {
    // 어떤 뷰를 만들 것인가
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder =
        QuoteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quote, parent, false)
        )

    // 어떤 뷰를 바인드할 것인가
    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val actualPosition = position % quotes.size  // 이게 핵심
        holder.bind(quotes[actualPosition], isNameRevealed)
    }

    // 몇개의 뷰를 보여줄 것인가
    override fun getItemCount(): Int = Int.MAX_VALUE // 끝은 있지만 아주 크다.

    class QuoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val quoteTextView = itemView.findViewById<TextView>(R.id.item_quote_text_view_content)
        private val nameTextView = itemView.findViewById<TextView>(R.id.item_quote_text_view_name)

        // 어떻게 렌더링할지 정하는 함수
        @SuppressLint("SetTextI18n")
        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTextView.text = "\"${quote.quote}\""
            if(isNameRevealed) {
                nameTextView.text = "- ${quote.name}"
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }

        }

    }
}
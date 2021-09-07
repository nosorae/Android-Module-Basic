package nosorae.changed_name.p12_book.model

import com.google.gson.annotations.SerializedName
// 이 프로젝트에서는 BestSellerDTO 랑 이거 둘 중 하나 만들면 되겠지만
// 확장성을 위해서 ( 다른 항목 추가될 수 있음 ) 따로 만든다.
data class SearchBookDTO(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)
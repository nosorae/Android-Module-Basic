package nosorae.module_basic.p12_book.model

import com.google.gson.annotations.SerializedName

// 전체 모델에서 데이터를 꺼내올 수 있게 연결시켜주는 개념을 DTO 라고 한다.
// 베스트셀러 DTO 로 전체 api 의 response 를 받아온다.
// 데이터 전송 객체(data transfer object)
data class BestSellerDTO(
    @SerializedName("title") val title: String,
    @SerializedName("item") val books: List<Book>
)

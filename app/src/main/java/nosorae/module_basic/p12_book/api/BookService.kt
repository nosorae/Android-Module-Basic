package nosorae.module_basic.p12_book.api

import nosorae.module_basic.p12_book.model.BestSellerDTO
import nosorae.module_basic.p12_book.model.SearchBookDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    // json 을 고정으로 하는 방법 ?output=json 쿼리에 output 이 키로 있고 value 를 json 주면 json 으로 결과 오니 미리 주소에 써놓기
    @GET("/api/search.api?output=json")
    fun getBookByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ): Call<SearchBookDTO>

    // output 은 json 으로 categoryId 는 100 으로 고정하는 쿼리를 url 에 추가
    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
        @Query("key") apiKey: String
    ): Call<BestSellerDTO>
}
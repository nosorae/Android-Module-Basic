package nosorae.module_basic.p14_market.home

data class ArticleModel(
    val sellerId: String,
    val title: String,
    val createdAt: Long,
    val price: String,
    val imageUrl: String
) {
    // realtime database 의 그대로 모델클래스를 사용하려면 빈 생성자가 꼭 필요하다.
    constructor(): this("", "", 0, "", "")
}

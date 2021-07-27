package nosorae.module_basic.p14_market.chatList

data class MarketChatListItem(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    // realtime database 에서 그대로 모델클래스를 사용하려면 빈 생성자가 꼭 필요하다.
    constructor(): this("", "", "", 0)
}

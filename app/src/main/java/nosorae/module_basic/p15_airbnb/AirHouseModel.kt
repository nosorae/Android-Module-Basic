package nosorae.module_basic.p15_airbnb

data class AirHouseModel(
    val id: Int,
    val title: String,
    val price: String,
    val imgUrl: String,
    val lat: Double,
    val lng: Double
)
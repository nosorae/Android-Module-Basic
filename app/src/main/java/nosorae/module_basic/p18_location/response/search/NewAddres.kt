package nosorae.module_basic.p18_location.response.search

data class NewAddres(
    val bldNo1: String,
    val bldNo2: String,
    val centerLat: String,
    val centerLon: String,
    val frontLat: String,
    val frontLon: String,
    val fullAddressRoad: String,
    val roadId: String,
    val roadName: String
)
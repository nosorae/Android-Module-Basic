package nosorae.module_basic.p18_location.response.search

data class SearchPoiInfoX(
    val count: String,
    val page: String,
    val pois: Pois,
    val totalCount: String
)
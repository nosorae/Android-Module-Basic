package nosorae.changed_name.p18_location.response.search

data class SearchPoiInfoX(
    val count: String,
    val page: String,
    val pois: Pois,
    val totalCount: String
)
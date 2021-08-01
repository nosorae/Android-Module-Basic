package nosorae.module_basic.p18_location.response.search

data class Poi(
    val collectionType: String,
    val detailAddrname: String,
    val detailBizName: String,
    val firstBuildNo: String,
    val firstNo: String,
    val frontLat: String,
    val frontLon: String,
    val groupSubLists: GroupSubLists,
    val id: String,
    val lowerAddrName: String,
    val lowerBizName: String,
    val middleAddrName: String,
    val middleBizName: String,
    val mlClass: String,
    val name: String,
    val navSeq: String,
    val newAddressList: NewAddressList,
    val noorLat: String,
    val noorLon: String,
    val pkey: String,
    val radius: String,
    val roadName: String,
    val rpFlag: String,
    val secondBuildNo: String,
    val secondNo: String,
    val upperAddrName: String,
    val upperBizName: String
)
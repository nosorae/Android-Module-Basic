package nosorae.changed_name.p18_location.utility

import nosorae.changed_name.p18_location.LocationKey
import nosorae.changed_name.p18_location.LocationUrl
import nosorae.changed_name.p18_location.response.geo.AddressInfoResponse
import nosorae.changed_name.p18_location.response.search.SearchPoiInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LocationService {
    @GET(LocationUrl.GET_TMAP_LOCATION)
    suspend fun getSearchLocation(
        @Header("appKey") appKey: String = LocationKey.TMAP_KEY,
        @Query("version") version: Int = 1,
        @Query("searchKeyword") keyWord: String,
        @Query("count") count: Int = 20
    ): Response<SearchPoiInfo>

    @GET(LocationUrl.GET_TMAP_GEO_CODE)
    suspend fun getGeoLocation(
        @Header("appKey") appKey: String = LocationKey.TMAP_KEY,
        @Query("version") version: Int = 1,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<AddressInfoResponse>
}
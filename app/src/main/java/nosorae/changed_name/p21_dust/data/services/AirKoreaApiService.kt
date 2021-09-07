package nosorae.changed_name.p21_dust.data.services

import retrofit2.Response
import nosorae.changed_name.BuildConfig
import nosorae.changed_name.p21_dust.data.models.airquality.AirQualityResponse
import nosorae.changed_name.p21_dust.data.models.monitoringstation.NearbyMonitoringStationsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    @GET(
        "B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList" +
                "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
                "&returnType=json"
    )
    suspend fun getNearbyMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<NearbyMonitoringStationsResponse>


    @GET("/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty" +
            "?serviceKey=${BuildConfig.AIR_KOREA_SERVICE_KEY}" +
            "&returnType=json" +
            "&dataTerm=DAILY" +
            "&ver=1.3")
    suspend fun getRealtimeAirQualities(
        @Query("stationName") stationName: String
    ): Response<AirQualityResponse>
}
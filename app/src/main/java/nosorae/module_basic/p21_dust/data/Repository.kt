package nosorae.module_basic.p21_dust.data

import nosorae.module_basic.BuildConfig
import nosorae.module_basic.p21_dust.data.models.airquality.MeasuredValue
import nosorae.module_basic.p21_dust.data.models.monitoringstation.MonitoringStation
import nosorae.module_basic.p21_dust.data.services.AirKoreaApiService
import nosorae.module_basic.p21_dust.data.services.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation? {
        val tmCoordinates = kakaoLocalApiService.getTmCoordinates(longitude, latitude)
            .body()
            ?.documents
            ?.firstOrNull()

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!, tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it.tm ?: Double.MAX_VALUE } // 여기서 tm 은 인자로 넣은 좌표와 측정소 간의 거리이다. 원소 중에 null 인 것이 있다면 MAX_VALUE 로 후순위로 밀려나게 한다

    }

    suspend fun getLatestAirQualityData(stationName: String): MeasuredValue? {
        return airKoreaApiService
            .getRealtimeAirQualities(stationName)
            .body()
            ?.response
            ?.body
            ?.measuredValues
            ?.firstOrNull() // 첫번째 것을 가져오는 이유는? 데이터 살펴보면 가장 최근에 측정된 순이기 때문이다.

    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DustUrl.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // 로깅추가를 클라이언트에 하니까!
            .build()
            .create() // 원래는 인자로 클래스를 전달해야하는데 코틀린 익스텐션에서는 알아서 알맞은 제네릭 클래스 타입을 넣어준다.
    }

    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(DustUrl.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient()) // 로깅추가를 클라이언트에 하니까!
            .build()
            .create() // 원래는 인자로 클래스를 전달해야하는데 코틀린 익스텐션에서는 알아서 알맞은 제네릭 클래스 타입을 넣어준다.
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    // 로깅 레벨에 따라서 보이는 정도가 결정된다.
                    level = if (BuildConfig.DEBUG) { // 실제 출시 되었을 때는 보안상의 문제로 로그를 남길 필요가 없으니 디버그일때만 로깅하게 설정
                        HttpLoggingInterceptor.Level.BODY

                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

}
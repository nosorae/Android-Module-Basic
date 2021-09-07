package nosorae.changed_name.p18_location.utility

import nosorae.changed_name.BuildConfig
import nosorae.changed_name.p18_location.LocationUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LocationRetrofitUtil {

    val apiService: LocationService by lazy {
        getRetrofit().create(LocationService::class.java)
    }


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LocationUrl.TMAP_URL)
            .addConverterFactory(GsonConverterFactory.create()) // response body 를 gson 으로 파싱
            .client(buildOkHttpClient())
            .build()
    }


    // api 호출할 때마다 로그를 찍어줄 수 있게 설정
    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

}
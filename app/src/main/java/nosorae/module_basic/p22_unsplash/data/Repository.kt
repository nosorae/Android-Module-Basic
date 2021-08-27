package nosorae.module_basic.p22_unsplash.data

import android.app.DownloadManager
import nosorae.module_basic.BuildConfig
import nosorae.module_basic.p22_unsplash.data.models.photo.PhotoResponse
import nosorae.module_basic.p22_unsplash.data.services.UnsplashApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object Repository {
    private val unsplashApiService: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(UnsplashUrl.UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
            .create()
    }

    suspend fun getRandomPhotos(query: String?): List<PhotoResponse>? =
        unsplashApiService.getRandomPhotos(query)?.body()

    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}
package nosorae.module_basic.p22_unsplash.data.services

import nosorae.module_basic.BuildConfig
import nosorae.module_basic.p22_unsplash.data.models.photo.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {
    @GET(
        "photos/random" +
            "?client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}" +
            "&count=30"
    )
    suspend fun getRandomPhotos(
        @Query("query") query: String?, // null 을 주면 그냥 랜덤하게 사진 가져오는 것이다.
    ): Response<List<PhotoResponse>>
}
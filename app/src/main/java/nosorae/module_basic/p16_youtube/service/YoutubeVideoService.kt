package nosorae.module_basic.p16_youtube.service

import nosorae.module_basic.p16_youtube.model.YoutubeVideoDTO
import retrofit2.Call
import retrofit2.http.GET

interface YoutubeVideoService {

    @GET("/v3/94e3c8c4-4c0e-431e-8280-9f1e6ab63d4b")
    fun listVideo() : Call<YoutubeVideoDTO>

}
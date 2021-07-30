package nosorae.module_basic.p17_music.service

import nosorae.module_basic.p17_music.model.MusicDTO
import retrofit2.Call
import retrofit2.http.GET

interface MusicService {
    @GET("/v3/60eae652-2471-4e03-a243-59b788968d7a")
    fun listMusics(): Call<MusicDTO>
}
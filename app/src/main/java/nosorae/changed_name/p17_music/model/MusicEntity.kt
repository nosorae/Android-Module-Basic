package nosorae.changed_name.p17_music.model

import com.google.gson.annotations.SerializedName

data class MusicEntity(
    @SerializedName("track")val track: String,
    @SerializedName("streamUrl")val streamUrl: String,
    @SerializedName("artist")val artist: String,
    @SerializedName("coverUrl")val coverUrl: String
)

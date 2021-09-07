package nosorae.changed_name.p22_unsplash.data.models.photo


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("download")
    val download: String?,
    @SerializedName("download_location")
    val downloadLocation: String?,
    @SerializedName("html")
    val html: String?,
    @SerializedName("self")
    val self: String?
)
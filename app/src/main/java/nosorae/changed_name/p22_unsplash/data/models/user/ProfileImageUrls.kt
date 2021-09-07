package nosorae.changed_name.p22_unsplash.data.models.user


import com.google.gson.annotations.SerializedName

data class ProfileImageUrls(
    @SerializedName("large")
    val large: String?,
    @SerializedName("medium")
    val medium: String?,
    @SerializedName("small")
    val small: String?
)
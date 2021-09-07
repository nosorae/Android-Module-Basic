package nosorae.changed_name.p22_unsplash.data.models.photo


import com.google.gson.annotations.SerializedName

data class Position(
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?
)
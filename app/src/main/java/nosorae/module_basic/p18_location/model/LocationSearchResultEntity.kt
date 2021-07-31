package nosorae.module_basic.p18_location.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSearchResultEntity(
    val fullAdress: String,
    val buildingName: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable

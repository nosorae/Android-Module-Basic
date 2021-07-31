package nosorae.module_basic.p18_location.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val lat: Float,
    val lng: Float
) : Parcelable

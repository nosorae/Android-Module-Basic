package nosorae.changed_name.p21_dust.data.models.airquality

import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val measuredValues: List<MeasuredValue>?,
    val numOfRows: Int?,
    val pageNo: Int?,
    val totalCount: Int?
)
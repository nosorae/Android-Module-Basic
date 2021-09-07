package nosorae.changed_name.p21_dust.data.models.monitoringstation

import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val monitoringStations: List<MonitoringStation>?,
    val numOfRows: Int?,
    val pageNo: Int?,
    val totalCount: Int?
)
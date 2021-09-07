package nosorae.changed_name.p21_dust.data.models.airquality

import com.google.gson.annotations.SerializedName

data class MeasuredValue(
    val coFlag: Any?,
    @SerializedName("coGrade")
    val coGrade: AirQualityGrade?,
    val coValue: String?,
    val dataTime: String?,
    val khaiGrade: AirQualityGrade?,
    val khaiValue: String?,
    val mangName: String?,
    val no2Flag: Any?,
    val no2Grade: AirQualityGrade?,
    val no2Value: String?,
    val o3Flag: Any?,
    val o3Grade: AirQualityGrade?,
    val o3Value: String?,
    val pm10Flag: Any?,
    val pm10Grade: AirQualityGrade?,
    val pm10Grade1h: String?,
    val pm10Value: String?,
    val pm10Value24: String?,
    val pm25Flag: Any?,
    val pm25Grade: AirQualityGrade?,
    val pm25Grade1h: String?,
    val pm25Value: String?,
    val pm25Value24: String?,
    val so2Flag: Any?,
    val so2Grade: AirQualityGrade?,
    val so2Value: String?
)